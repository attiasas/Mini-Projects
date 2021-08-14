package NLP.Search_Engine.src.ViewModel;

import Model.MyModel;
import Model.SearchEngine.Index.Indexer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created By: Assaf Attias
 * On 12/11/2019
 * Description: Represents a ViewModel in MVVM architecture that links a logic model with GUI view
 */
public class MyViewModel extends Observable implements Observer
{
    private MyModel model;

    /**
     * dynamic Injection for model to listen
     * @param myModel
     */
    public MyViewModel(MyModel myModel)
    {
        model = myModel;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if(o == model)
        {
            if(!(arg instanceof double[]) && (arg instanceof Object[]))
            {
                Object[] oArg = (Object[])arg;
                if(oArg.length == 3)
                {
                    // single
                    oArg[oArg.length - 1] = translateResult((List<Indexer.DocumentEntry>)oArg[oArg.length - 1]);
                }
                else
                {
                    // multiple
                    HashMap<String,ObservableList<QueryResultModel>> translated = new HashMap<>();
                    HashMap<String,List<Indexer.DocumentEntry>> result = (HashMap<String,List<Indexer.DocumentEntry>>)oArg[oArg.length - 1];

                    for(Map.Entry<String,List<Indexer.DocumentEntry>> res : result.entrySet())
                    {
                        translated.put(res.getKey(),translateResult(res.getValue()));
                    }

                    oArg[oArg.length - 1] = translated;
                }
            }

            setChanged();
            notifyObservers(arg);
        }
    }


    //<editor-fold desc="View.Main Controller Methods">

    public class DictionaryEntryModel
    {
        private final SimpleStringProperty term;
        private final SimpleIntegerProperty count;

        public DictionaryEntryModel(Indexer.DictionaryEntry entry)
        {
            this.term = new SimpleStringProperty(entry.term);
            this.count = new SimpleIntegerProperty(entry.count);
        }

        public String getTerm() {
            return term.get();
        }

        public SimpleStringProperty termProperty() {
            return term;
        }

        public int getCount() {
            return count.get();
        }

        public SimpleIntegerProperty countProperty() {
            return count;
        }

        public void setTerm(String term) {
            this.term.set(term);
        }

        public void setCount(int count) {
            this.count.set(count);
        }
    }

    public class QueryResultModel
    {
        private final SimpleIntegerProperty rank;
        private final SimpleStringProperty docId;

        private final SimpleStringProperty entity1;
        private final SimpleStringProperty entity2;
        private final SimpleStringProperty entity3;
        private final SimpleStringProperty entity4;
        private final SimpleStringProperty entity5;

        public QueryResultModel(int rank, Indexer.DocumentEntry entry)
        {
            this.rank = new SimpleIntegerProperty(rank);
            this.docId = new SimpleStringProperty(entry.docName);

            int entryNumEntities = 0;
            for(int i = 0; i < entry.entities.length && entry.entities[i] != null; i++) entryNumEntities++;

            DecimalFormat decimalFormat = new DecimalFormat("#.#####");

            this.entity1 = new SimpleStringProperty(entryNumEntities > 0 ? (entry.entities[0] + " (" + decimalFormat.format(entry.score[0]) + ")") : "");
            this.entity2 = new SimpleStringProperty(entryNumEntities > 1 ? (entry.entities[1] + " (" + decimalFormat.format(entry.score[1]) + ")") : "");
            this.entity3 = new SimpleStringProperty(entryNumEntities > 2 ? (entry.entities[2] + " (" + decimalFormat.format(entry.score[2]) + ")") : "");
            this.entity4 = new SimpleStringProperty(entryNumEntities > 3 ? (entry.entities[3] + " (" + decimalFormat.format(entry.score[3]) + ")") : "");
            this.entity5 = new SimpleStringProperty(entryNumEntities > 4 ? (entry.entities[4] + " (" + decimalFormat.format(entry.score[4]) + ")") : "");
        }

        public int getRank() {
            return rank.get();
        }

        public SimpleIntegerProperty rankProperty() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank.set(rank);
        }

        public String getDocId() {
            return docId.get();
        }

        public SimpleStringProperty docIdProperty() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId.set(docId);
        }

        public String getEntity1() {
            return entity1.get();
        }

        public SimpleStringProperty entity1Property() {
            return entity1;
        }

        public void setEntity1(String entity1) {
            this.entity1.set(entity1);
        }

        public String getEntity2() {
            return entity2.get();
        }

        public SimpleStringProperty entity2Property() {
            return entity2;
        }

        public void setEntity2(String entity2) {
            this.entity2.set(entity2);
        }

        public String getEntity3() {
            return entity3.get();
        }

        public SimpleStringProperty entity3Property() {
            return entity3;
        }

        public void setEntity3(String entity3) {
            this.entity3.set(entity3);
        }

        public String getEntity4() {
            return entity4.get();
        }

        public SimpleStringProperty entity4Property() {
            return entity4;
        }

        public void setEntity4(String entity4) {
            this.entity4.set(entity4);
        }

        public String getEntity5() {
            return entity5.get();
        }

        public SimpleStringProperty entity5Property() {
            return entity5;
        }

        public void setEntity5(String entity5) {
            this.entity5.set(entity5);
        }
    }

    public ObservableList<QueryResultModel> translateResult(List<Indexer.DocumentEntry> entries)
    {
        if(entries == null) return null;

        ArrayList<QueryResultModel> termModels = new ArrayList<>(entries.size());
        for(int i = 0; i < entries.size(); i++)
        {
            termModels.add(new QueryResultModel(i+1,entries.get(i)));
        }

        return FXCollections.observableList(termModels);
    }

    public ObservableList<DictionaryEntryModel> getDictionaryEntries()
    {
        Collection<Indexer.DictionaryEntry> entries = model.getDictionary();
        if(entries == null || entries.isEmpty()) return null;
        ArrayList<DictionaryEntryModel> termModels = new ArrayList<>(entries.size());
        for(Indexer.DictionaryEntry entry : entries)
        {
            termModels.add(new DictionaryEntryModel(entry));
        }
        termModels.sort(Comparator.comparing(dictionaryEntryModel -> dictionaryEntryModel.term.get()));

        return FXCollections.observableList(termModels);
    }

    /**
     * Send a build command to model base on the parameters that was input
     * @param dataPath
     * @param postPath
     */
    public void buildIndex(String dataPath, String postPath, boolean stemming) { model.buildIndex(dataPath,postPath, stemming); }

    /**
     * Send a loadIndex command to model base on the parameters that was input
     * @param path
     */
    public void loadIndex(String path, boolean stemming) { model.loadIndex(path, stemming); }

    /**
     * Send a deleteIndex command to model base on the parameters that was input
     * @param path
     */
    public void deleteIndex(String path, boolean stemming) { model.deleteIndex(path, stemming); }

    public void multipleQueries(File queriesFile, File resultDir, boolean semantic, boolean clickStream) { model.multipleQueries(queriesFile,resultDir,semantic,clickStream); }

    public void singleQuery(String query, File resultDir, boolean semantic, boolean clickStream) { model.singleQuery(query,resultDir,semantic,clickStream); }
    //</editor-fold>
}
