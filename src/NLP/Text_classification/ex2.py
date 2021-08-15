"""
## Imports & Constants

### imports
"""
# general
import os,torch,time
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import warnings
warnings.filterwarnings('ignore', 'Solver terminated early.*')
# pre-process
from nltk.tokenize import TweetTokenizer
from datetime import datetime
from sklearn.preprocessing import MinMaxScaler

# models
from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC
from sklearn.ensemble import RandomForestClassifier
from torch import nn
from torch.optim import Adam
from sklearn.dummy import DummyClassifier
# compare
from sklearn import metrics
from sklearn.model_selection import GridSearchCV
# save/load
import pickle

"""### constants"""
train_file_path = "trump_train.tsv"
test_file_path = "trump_test.tsv"

best_model_file_path = "best_model"

# RAW DATA ==============
ID = "ID"
TEXT = "TEXT"
TOKENS = "TOKENS"
HANDLE = "HANDLE"
TIME_STAMP = "TIME_STAMP"
# FEATURES ==============
realDonaldTrump = "realDonaldTrump" # One-Hot Encoder of HANDLE
POTUS = "POTUS"                     # One-Hot Encoder of HANDLE
PressSec = "PressSec"               # One-Hot Encoder of HANDLE
TIME_STAMP_INT = "TIME_STAMP_INT"   # Time stamp in int format
LEN = "LEN"                         # number of tokens in the TEXT
NUM_URL = "NUM_URL"                 # number of urls (https) in the TEXT
NUM_TAGS = "NUM_TAGS"               # number tags (@ before name) in TEXT
NUM_HASH = "NUM_HASH"               # number tags (# before word) in TEXT
NUM_UPPER = "NUM_UPPER"             # number of upper case chars in the TEXT
# Y =====================
LABEL = "LABEL"
# =======================

TRUMP_LABEL_TEXT = "android"
TIME_STAMP_FORMAT = '%Y-%m-%d %H:%M:%S'
TIME_STAMP_FORMAT_TO_INT = '%Y%m%d'

"""# **Load Data**"""

def read_tsv_file(empty_data,f_path):
    """
    Read tsv file and load its data into a given dict
    :param empty_data: dict with col to insert info into it
    :param f_path: tsv file path to read
    """
    with open(f_path,'r') as file:
        lines = file.readlines()
        for line in lines:
            line = line[:len(line)-1]
            tokens = line.split("\t")
            for i,col in enumerate(empty_data):
                empty_data[col].append(tokens[i])

def load_data(train_f_path=train_file_path, test_f_path=test_file_path):
    """
    Load train and test data from file into a pandas Dataframe
    :param train_f_path: train file path
    :param test_f_path:  test file path
    :return: train_df, test_df: data frames with data
    """
    # <tweet id> <user handle> <tweet text> <time stamp> <device>
    # Test - This file lacks the <tweet id> and <device> fields.
    train_data = {ID:[],HANDLE:[],TEXT:[],TIME_STAMP:[],LABEL:[]}
    read_tsv_file(train_data,train_f_path)

    test_data = {HANDLE:[],TEXT:[],TIME_STAMP:[]}
    read_tsv_file(test_data,test_f_path)

    train_df = pd.DataFrame(train_data)
    test_df = pd.DataFrame(test_data)
  
    return train_df, test_df

"""# **Pre-Process**

## pre process
"""

# word2vec
from torchtext.vocab import GloVe
embedding_glove = GloVe(name='840B', dim=300)

def preprocess_data(data_set):
    """
    Apply Pre-Process to a given dataset, clean the raw data and produce features from it
    missing value = Drop row, translate LABEL to int (binary classification), transform date to int value
    Tokenize TEXT with TweetTokenizer, and create Features (see at the start of the file)
    :param data_set: data set to preprocess
    """
  
    data_set = data_set.dropna() # drop rows with missing values

    data_set[TIME_STAMP_INT] = data_set.apply(lambda row: int(datetime.strptime(row.TIME_STAMP,TIME_STAMP_FORMAT).strftime(TIME_STAMP_FORMAT_TO_INT)),axis=1) # convert timestamp to int

    one_hot = pd.get_dummies(data_set[HANDLE])
    data_set = data_set.drop(HANDLE,axis = 1)
    data_set = data_set.join(one_hot)

    if LABEL in data_set.columns:
        data_set[LABEL] = data_set.apply(lambda row: 0 if row.LABEL == TRUMP_LABEL_TEXT else 1,axis=1) # replace label with numeric value

    tokenizer = TweetTokenizer(preserve_case=False,strip_handles=True,reduce_len=True) # tokenize words
    data_set[TOKENS] = data_set.apply(lambda row: tokenizer.tokenize(row.TEXT),axis=1) # tokenize

    # create TEXT embedding
    embedded = []
    for i in range(len(data_set)):
        emb_row = np.asarray([embedding_glove[t].numpy() for t in data_set.iloc[i].TOKENS])
        embedded.append(list(emb_row.sum(axis=0)))
  
    embedded = np.asarray(embedded)

    scalar = MinMaxScaler()

    data_set[LEN] = scalar.fit_transform(data_set.apply(lambda row: len(row.TOKENS),axis=1).to_numpy().reshape(-1,1))
    data_set[NUM_URL] = scalar.fit_transform(data_set.apply(lambda row: row.TEXT.count("https"),axis=1).to_numpy().reshape(-1,1))
    data_set[NUM_TAGS] = scalar.fit_transform(data_set.apply(lambda row: row.TEXT.count("@"),axis=1).to_numpy().reshape(-1,1))
    data_set[NUM_HASH] = scalar.fit_transform(data_set.apply(lambda row: row.TEXT.count("#"),axis=1).to_numpy().reshape(-1,1))
    data_set[NUM_UPPER] = scalar.fit_transform(data_set.apply(lambda row: sum(1 for c in row.TEXT if c.isupper()),axis=1).to_numpy().reshape(-1,1))
  
    return data_set,embedded

"""## Prepare test/train dataset for model"""

def transform_dataset_to_X_y(data_set,features,embedded):
    """
    Transform Dataframe to a X,y set to fit/predict with ml models, if LABEL col exists also creates y set
    :param data_set: dataset to transform
    :param features: feature column list to take from the dataset
    :param embedded: embedded word2vec for tokens in the dataset to use as features
    :return: X (y if LABEL exists)
    """
    features_df = data_set[features]

    extra_features = []
    for col in features_df.columns:
        t = data_set[col].to_numpy().reshape(-1,1)

        extra_features.append(t)
    extra_features.append(embedded)
    X = np.hstack(extra_features)

    if LABEL in data_set:
        y = data_set[LABEL]
        return X, y

    return X

"""# **Train the models**

## Metrics, Hyper Parameters Optimization and Cross-Validation

### Metrics
"""

from sklearn.metrics import make_scorer, accuracy_score, classification_report

scorer = {'AUC': 'roc_auc', 'Accuracy': make_scorer(accuracy_score), 'Precision': 'average_precision', 'Recall': 'recall_weighted', 'F': 'f1'}

"""### Hyper Parameter Optimization and Cross-Validation"""

def get_best_model_grid_search(clf,parameters, X, y, verbose=False):
    """
    Apply Grid Search Cross Validation for Hyper parameters of a given classifier and return the best one
    :param clf: classifier to apply on it
    :param parameters: model parameters to use in search
    :param X: X train set
    :param y: y label set
    :param verbose: print results
    :return: best_estimator, cv_scores
    """
    grid_obj = GridSearchCV(clf, parameters, scoring=scorer, return_train_score=True, refit='Accuracy')
    start = round(time.time())
    grid_obj = grid_obj.fit(X, y)

    scores = pd.DataFrame(grid_obj.cv_results_)
    if verbose:
        print("Search Time: " + str(round(time.time()) - start) + " sec")
        print("----")
        print("best params: " + str(grid_obj.best_params_))
        print("----")
        print("Best Accuracy score: " + str(grid_obj.best_score_))
        print("----")

    return grid_obj.best_estimator_, scores[[col for col in scores if col.startswith('param') or col.startswith('mean') or col.startswith('std')]]

"""## Models"""

"""### LogisticRegression"""

logistic_parameters = {'max_iter': [int(x) for x in np.linspace(start = 10000, stop = 12000, num = 2)],
                       'C': [float(x) for x in np.linspace(start = 0.5, stop = 1.0, num = 5)]}

logistic_model_features = [NUM_URL,NUM_TAGS,NUM_HASH,LEN]  #[NUM_URL,NUM_TAGS,NUM_HASH,NUM_UPPER,LEN,TIME_STAMP_IN]

"""### SVC"""

svc_parameters = {'kernel': ['linear', 'rbf'], 'C': [float(x) for x in np.linspace(start = 1.2, stop = 1.5, num = 3)],
                  'gamma': ['scale', 'auto'], 'max_iter': [100000]}

svc_model_features = [NUM_URL,NUM_TAGS,NUM_HASH,LEN] #[NUM_URL,NUM_TAGS,NUM_HASH,NUM_UPPER,LEN,TIME_STAMP_INT,realDonaldTrump,PressSec]

"""### Random Forest"""

random_forest_parameters = {'n_estimators': [int(x) for x in np.linspace(start = 50, stop = 150, num = 3)],
                            'max_features': ['log2','auto'], 'criterion': ['entropy', 'gini'],
                            'max_depth': [20,30], 'min_samples_split': [5], 'min_samples_leaf': [5,8,10]}

random_forest_model_features = [NUM_URL,NUM_TAGS,NUM_HASH,LEN]

"""### FFNN classifier"""

nn_parameters = { "val_train_ratio": 0.7, "hidden_dim": 150, "hidden2_dim": 50,"hidden3_dim": 20, "target_dim":1,
                  "learning_rate" : 0.01, "loss": nn.BCELoss(), "max_epochs": 100, "more_loss_epoch_tol" : 5,
                  "optimizer" : Adam}

nn_model_features = [NUM_URL,NUM_TAGS,NUM_HASH,LEN]

class FFNN:
    """
    FFNN - implementation of fit/predict with nn.sequential
    """
    def __init__(self, model, parameters):
        super().__init__()
        self.model = model
        self.parmeters = parameters

    def predict(self,X):
        """
        predict labels for a given input
        :param X: samples to predict, ndarray with shape (n_samples,n_features)
        :return: predicted labels of samples
        """
        X = torch.Tensor(X)
        prediction = self.model(X)
        return (prediction > 0.5).float().numpy().reshape(-1)

    def fit(self,X_train,y_train,use_val_set=False, verbose=False):
        """
        Train NN model
        :param X_train: samples to train on
        :param y_train: label of samples
        :param use_val_set: bool, use validation set with the given input
        :param verbose: print train process
        """

        y_train = y_train.to_numpy()
      
        if use_val_set:
            X_val = X_train[round(self.parmeters["val_train_ratio"] * len(X_train)):]
            X_val = torch.Tensor(X_val)
            y_val = y_train[round(self.parmeters["val_train_ratio"] * len(y_train)):]
            y_val = y_val.reshape(-1,1)
            y_val = torch.Tensor(y_val)

            X_train = X_train[:round(self.parmeters["val_train_ratio"] * len(X_train))]
            y_train = y_train[:round(self.parmeters["val_train_ratio"] * len(y_train))]

        X_train = torch.Tensor(X_train)
        y_train = y_train.reshape(-1,1)
        y_train = torch.Tensor(y_train)

        self.model.train()

        loss_function = self.parmeters["loss"]
        optimizer = self.parmeters["optimizer"](self.model.parameters(), lr=self.parmeters["learning_rate"])
        epochs = self.parmeters["max_epochs"]
        start_t = time.time()

        last_val_loss = 1000
        less_val_count = 0
        max_val_count = 5
        loss = -1

        for epoch in range(epochs):
        
            epoch_losses = list()
            epoch_acc = list()
            start_e_time = time.time()

            optimizer.zero_grad()

            prediction = self.model(X_train)
            loss = loss_function(prediction, y_train)

            loss.sum().backward()
            optimizer.step()
          
            output = (prediction > 0.5).float()
            correct = (output == y_train).float().sum()
            epoch_acc.append(correct/y_train.shape[0])
            epoch_losses.append(loss.sum().item())

            if use_val_set:
                val_acc = list()
                val_losses = list()
                with torch.no_grad():
                    optimizer.zero_grad()
                    prediction = self.model(X_val)

                    loss = loss_function(prediction, y_val)
                    output = (prediction > 0.5).float()
                    correct = (output == y_val).float().sum()
                    val_acc.append(correct/y_val.shape[0])
                    val_losses.append(loss.sum().item())

            elapsed_e_time = time.time() - start_e_time
            if verbose:
                if use_val_set:
                    print('epoch {}:  {:.3f} sec  loss : {:.3f} acc : {:.3f} | val_loss : {:.3f} val_acc : {:.3f}'.format(epoch,elapsed_e_time,np.mean(epoch_losses),np.mean(epoch_acc),np.mean(val_losses),np.mean(val_acc)))
                else:
                    print('epoch {}:  {:.3f} sec  loss : {:.3f} acc : {:.3f}'.format(epoch,elapsed_e_time,np.mean(epoch_losses),np.mean(epoch_acc)))
            if loss > last_val_loss:
                less_val_count += 1
                if less_val_count >= max_val_count:
                    self.parmeters["max_epochs"] = epoch
                    break
            else:
                less_val_count = 0
                last_val_loss = loss
        if verbose:
            print("Fit Time: " + str(round(time.time()) - start_t) + " sec")
        self.model.eval()

def prepare_assignment():
    """
    apply all pipeline for preparing the assignment interface, train all models on train set
    compare results and save the best
    """
    train_set, test_set = load_data()

    train_set, train_embedded_mat = preprocess_data(train_set)
    test_set, test_embedded_mat = preprocess_data(test_set)

    models = []

    # Logistic Reg
    logistic_reg_X_train, logistic_reg_y_train = transform_dataset_to_X_y(train_set, logistic_model_features,train_embedded_mat)
    logistic_reg_X_test = transform_dataset_to_X_y(test_set, logistic_model_features, test_embedded_mat)

    logistic_reg_model, logistic_reg_cv_score = get_best_model_grid_search(LogisticRegression(), logistic_parameters,logistic_reg_X_train, logistic_reg_y_train,True)
    models.append(('Logistic Regression', logistic_reg_model, (logistic_reg_X_train, logistic_reg_y_train), logistic_reg_X_test))

    # SVC
    svc_X_train, svc_y_train = transform_dataset_to_X_y(train_set, svc_model_features, train_embedded_mat)
    svc_X_test = transform_dataset_to_X_y(test_set, svc_model_features, test_embedded_mat)

    svc_model, svc_cv_score = get_best_model_grid_search(SVC(), svc_parameters, svc_X_train, svc_y_train, True)
    models.append(('SVC', svc_model, (svc_X_train, svc_y_train), svc_X_test))

    # Random Forest
    random_forest_X_train, random_forest_y_train = transform_dataset_to_X_y(train_set, random_forest_model_features,train_embedded_mat)
    random_forest_X_test = transform_dataset_to_X_y(test_set, random_forest_model_features, test_embedded_mat)

    random_forest_model, random_forest_cv_score = get_best_model_grid_search(RandomForestClassifier(),random_forest_parameters,random_forest_X_train,random_forest_y_train, True)
    models.append(('Random Forest', random_forest_model, (random_forest_X_train, random_forest_y_train), random_forest_X_test))

    # FFNN
    nn_X_train, nn_y_train = transform_dataset_to_X_y(train_set, nn_model_features, train_embedded_mat)
    nn_X_test = transform_dataset_to_X_y(test_set, nn_model_features, test_embedded_mat)

    nn_layers = nn.Sequential(nn.Linear(len(nn_X_train[0]), nn_parameters["hidden_dim"]),
                              nn.ReLU(),
                              nn.Dropout(p=0.3),
                              nn.Linear(nn_parameters["hidden_dim"], nn_parameters["hidden2_dim"]),
                              nn.ReLU(),
                              nn.Dropout(p=0.3),
                              nn.Linear(nn_parameters["hidden2_dim"], nn_parameters["hidden3_dim"]),
                              nn.ReLU(),
                              nn.Linear(nn_parameters["hidden3_dim"], nn_parameters["target_dim"]),
                              nn.Sigmoid())

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    nn_model = FFNN(nn_layers.to(device), nn_parameters)
    nn_model.fit(nn_X_train, nn_y_train, use_val_set=True, verbose=True)

    models.append(('FFNN', FFNN(nn_layers.to(device), nn_parameters), (nn_X_train, nn_y_train), nn_X_test))

    """# **Test - Compare models**

    ## Test and Compare
    """
    baseline_model = DummyClassifier(strategy="most_frequent")
    baseline_model.fit(logistic_reg_X_train, logistic_reg_y_train)
    models.append(('BaseLine', baseline_model, (logistic_reg_X_train, logistic_reg_y_train), None))

    msgs = []
    best_model_idx = -1
    best_acc = 0

    fig, ax = plt.subplots(1, 1, figsize=(15, 10))

    for i, (model_name, model, all_train_set, _) in enumerate(models):
        acc = 0
        elapsed_time = time.time()
        (X_train, y_train) = all_train_set

        if model_name == "FFNN":
            # train
            model.fit(X_train, y_train)
            # predict (eval acc)
            y_pred = model.predict(X_train)
            y_train = y_train.to_numpy()
            correct = np.count_nonzero((y_pred == y_train))
            acc = correct / y_train.shape[0]
            # roc
            fpr, tpr, threshold = metrics.roc_curve(y_train, y_pred)
            roc_auc = metrics.auc(fpr, tpr)
            ax.plot(fpr, tpr, label='FFNN (AUC = %0.2f)' % roc_auc)
        else:
            # train
            model.fit(X_train, y_train)
            # predict (eval acc)
            acc = model.score(X_train, y_train)
            # roc
            metrics.plot_roc_curve(model, X_train, y_train, name=model_name, ax=ax)

        if acc > best_acc:
            best_model_idx = i
            best_acc = acc

        msgs.append("{}, Accuracy: {}, Time To Process: {}".format(model_name, acc, round(time.time() - elapsed_time, 3)))

    ax.set_title('Model Comparison - ROC Curve')
    plt.show()

    for i, msg in enumerate(msgs):
        if i == best_model_idx:
            msg = " ".join([msg, ">> Best Model <<"])
        print(msg)

    """## save best model"""

    best_model = models[best_model_idx]
    (model_name, model, all_train_set, _) = best_model

    filehandler = open(best_model_file_path, 'wb')
    pickle.dump(model, filehandler)
    filehandler = open(best_model_file_path + "_params", 'wb')
    pickle.dump(model.get_params(), filehandler)

"""# **Assignment Interface**"""

def load_best_model():
    """
    Your best performing trained model
    returning your best performing model that was saved as part of the submission bundle
    as a pickle file or any format you like, as long it can load it
    """
    filehandler = open(best_model_file_path, 'rb')
    model_info = pickle.load(filehandler)
    model = model_info
    return model

def train_best_model():
    """
    training a classifier from scratch (should be the same classifier and parameters returned by load_best_model()
    Of course, the final model could be slightly different than the one returned by  load_best_model(), due to randomization issues.
    This function will learn the parameters based on a training set provided to it. The format of the training set is as described above.
    You could assume that the training set file (.tsv) is in the current directory. It should trigger the preprocessing and the whole pipeline
    """
    filehandler = open(best_model_file_path + "_params", 'rb')
    params = pickle.load(filehandler)
    model = RandomForestClassifier(**params)

    # read
    train_set = {ID: [], HANDLE: [], TEXT: [], TIME_STAMP: [], LABEL: []}
    read_tsv_file(train_set, train_file_path)
    train_set = pd.DataFrame(train_set)
    # pre-process
    train_set, train_emb = preprocess_data(train_set)
    # prepare data set for predict base on model
    X_train, y_train = transform_dataset_to_X_y(train_set, random_forest_model_features, train_emb)
    # fit
    model.fit(X_train, y_train)
    return model

def predict(m, fn):
    """
    m is the trained model and fn is the full path to a file in the same format as the test set (see above).
    predict(m, fn) returns a list of 0s and 1s, corresponding to the lines in the specified file
    """
    # read
    test_set = {HANDLE: [], TEXT: [], TIME_STAMP: []}
    read_tsv_file(test_set, fn)
    test_set = pd.DataFrame(test_set)
    # pre-process
    test_set, test_emb = preprocess_data(test_set)
    # prepare data set for predict base on model
    X_test = transform_dataset_to_X_y(test_set, random_forest_model_features, test_emb)
    # predict
    y_pred = m.predict(X_test)
    return list(y_pred)
