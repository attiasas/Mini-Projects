package NLP.Search_Engine.src.View;

import Model.SearchEngine.Index.Indexer;
import ViewModel.MyViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created By: Assaf Attias
 * On 12/11/2019
 * Description: Represents a View in MVVM architecture that controls the GUI in the application.
 *
 * Controls 3 tabs/views:    1. "Data Options" (chose paths and preform actions on index).
 *                           2. "Dictionary"   (view current dictionary of the index).
 *                           3. "Query"        (query the current file index to search for documents).
 */
public class View implements Observer, Initializable
{
    private MyViewModel viewModel;

    @FXML
    public TabPane tp_tabs;
    // Data Options
    public TextField tf_dataPath, tf_postPath;
    public Button b_browseDataPath, b_browsePostPath, b_build, b_load, b_delete;
    public CheckBox cb_stemming;
    // Dictionary
    public TableView<MyViewModel.DictionaryEntryModel> tv_dictionaryTable;
    public Tab dictionaryTab;
    public TableColumn tc_term;
    public TableColumn tc_count;
    // Query
    public Tab queryTab;
    public TableView<MyViewModel.QueryResultModel> tv_queryResult;
    public TextField tf_queryPath,tf_resultPath,tf_query;
    public Button b_browseQuery,b_browseResult,b_runQuery,b_runQueries;
    public CheckBox cb_semantic,cb_entities,cb_click,cb_save;
    public Label l_resultInfo;

    private ObservableList<MyViewModel.QueryResultModel> singleResult;

    /**
     * Set viewModel to send commands to.
     * @param viewModel - not null
     */
    public void setViewModel(MyViewModel viewModel)
    {
        if(viewModel == null) throw new IllegalArgumentException();
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        cb_entities.selectedProperty().addListener((observable, oldValue, newValue) -> setQueryTable(newValue));
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if(o == viewModel)
        {
            if(arg instanceof double[])
            {
                // build complete, show alert
                double[] dArg = (double[])arg;
                DecimalFormat decimalFormat = new DecimalFormat("#,##0");

                String buildResult = "Number Of Documents Found: " + decimalFormat.format((int)(dArg[0])) + ".\n" +
                                     "Number Of Terms Found: " + decimalFormat.format((int)(dArg[1])) + ".\n" +
                                     "time elapsed while processing: " + dArg[2] + " sec.";

                showAlert("Build Done","Build Action Succeeded",buildResult);

                setDictionaryTable(false);
            }
            else if("DELETED".equals(arg))
            {
                setDictionaryTable(true);
            }
            else if("LOAD".equals(arg))
            {
                setDictionaryTable(false);
                SingleSelectionModel<Tab> selectionModel = tp_tabs.getSelectionModel();
                selectionModel.select(queryTab);
            }
            else if(arg instanceof Object[])
            {
                Object[] oArg = (Object[])arg;

                if(oArg.length == 2)
                {
                    // multiple queries completed
                    double elapsed = (double)(oArg[0]);
                    HashMap<String,ObservableList<MyViewModel.QueryResultModel>> results = (HashMap<String,ObservableList<MyViewModel.QueryResultModel>>)oArg[1];

                    l_resultInfo.setText(results.size() + " Queries, Completed in " + elapsed + " (sec).");
                    singleResult = null;

                    for(Map.Entry<String,ObservableList<MyViewModel.QueryResultModel>> result : results.entrySet())
                    {
                        showResultInNewWindow(result.getKey().trim(),result.getValue());
                    }
                }
                else if(oArg.length == 3)
                {
                    // single query complete
                    double elapsed = (double)(oArg[0]);
                    int numOfDocs = (int)oArg[1];
                    ObservableList<MyViewModel.QueryResultModel> result = (ObservableList<MyViewModel.QueryResultModel>)(oArg[2]);

                    l_resultInfo.setText("Query Completed in " + elapsed + " (sec), showing " + result.size() + "/" + numOfDocs + " results.");
                    singleResult = result;
                }

                setQueryTable(cb_entities.isSelected());
            }
        }
    }


    //<editor-fold desc="General Methods">
    public void showResultInNewWindow(String query, ObservableList<MyViewModel.QueryResultModel> queryResult)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("QueryResult.fxml"));
            Parent root = fxmlLoader.load();

            if(root != null)
            {
                Stage queryViewStage = new Stage();
                queryViewStage.setTitle("Results For The Query: '" + query + "'");
                queryViewStage.setScene(new Scene(root));

                QueryView queryView = fxmlLoader.getController();
                queryView.setParameters(queryResult,cb_entities.isSelected());

                queryViewStage.initModality(Modality.APPLICATION_MODAL);
                queryViewStage.show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setDictionaryTable(boolean reset)
    {
        ObservableList<MyViewModel.DictionaryEntryModel> entries = viewModel.getDictionaryEntries();
        if(entries == null || reset)
        {
            dictionaryTab.setDisable(true);
            queryTab.setDisable(true);

            tv_dictionaryTable.setItems(null);
        }
        else
        {
            tc_term.setCellValueFactory(new PropertyValueFactory<MyViewModel.DictionaryEntryModel,String>("term"));
            tc_count.setCellValueFactory(new PropertyValueFactory<MyViewModel.DictionaryEntryModel,Integer>("count"));

            tv_dictionaryTable.setItems(entries);

            dictionaryTab.setDisable(false);
            queryTab.setDisable(false);
        }
    }

    /**
     * Show an alert window base on a given parameters
     * @param alertHeader - header that indicates what is wrong
     * @param alertMessage - message to instruct the user how to change
     */
    private void showAlert(String alertTitle, String alertHeader, String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(alertHeader);
        alert.setTitle(alertTitle);
        alert.setContentText(alertMessage);
        alert.show();
    }
    //</editor-fold>

    //<editor-fold desc="Dictionary">
    /**
     * Open Browse window to chose a Corpus directory.
     */
    public void browseDataPath()
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Browse for a corpus directory");
        File chosen = directoryChooser.showDialog(b_browseDataPath.getScene().getWindow());

        if (chosen != null && chosen.isDirectory())
        {
            tf_dataPath.setText(chosen.getPath());
        }
    }

    /**
     * Open Browse window to chose a directory to save information file to.
     */
    public void browsePostPath()
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Browse for a directory to save information to");
        File chosen = directoryChooser.showDialog(b_browsePostPath.getScene().getWindow());

        if (chosen != null && chosen.isDirectory())
        {
            tf_postPath.setText(chosen.getPath());
        }
    }

    /**
     * Send a build command to model base on the parameters that was input
     */
    public void build()
    {
        String dataPath = tf_dataPath.getText();
        String postPath = tf_postPath.getText();

        if(dataPath != null && postPath != null)
        {
            File dFile = new File(dataPath);
            File pFile = new File(postPath);

            // check input
            if(dFile.isDirectory() && pFile.isDirectory())
            {
                setDictionaryTable(true);
                // command
                viewModel.buildIndex(dataPath,postPath,cb_stemming.isSelected());
            }
            else
            {
                // not a directory, show alert
                showAlert("Alert","Path is not a directory","Data or/and Posting paths are not directories");
            }
        }
    }

    /**
     * Send a loadIndex command to model base on the parameters that was input
     */
    public void load()
    {
        String path = tf_postPath.getText();

        if(path != null && !path.equals(""))
        {
            viewModel.loadIndex(path,cb_stemming.isSelected());
        }
        else
        {
            // not a directory, show alert
            showAlert("Alert","Path is empty","Posting path is empty");
        }
    }

    /**
     * Send a deleteIndex command to model base on the parameters that was input
     */
    public void delete()
    {
        String path = tf_postPath.getText();

        if(path == null || path.equals(""))
        {
            showAlert("Alert","Path is empty","deleteIndex path is empty (in posting/data)");
        }
        else
        {
            viewModel.deleteIndex(path, cb_stemming.isSelected());
            queryTab.setDisable(true);
            dictionaryTab.setDisable(true);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Query">
    public void setQueryTable(boolean entities)
    {
        tv_queryResult.getColumns().clear();

        if(singleResult != null)
        {
            TableColumn tc_docRank = new TableColumn("#");
            tc_docRank.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,Integer>("rank"));
            tv_queryResult.getColumns().add(tc_docRank);

            TableColumn tc_docId = new TableColumn("ID");
            tc_docId.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("docId"));
            tv_queryResult.getColumns().add(tc_docId);

            if(entities)
            {
                TableColumn tc_e1 = new TableColumn("Entity1");
                TableColumn tc_e2 = new TableColumn("Entity2");
                TableColumn tc_e3 = new TableColumn("Entity3");
                TableColumn tc_e4 = new TableColumn("Entity4");
                TableColumn tc_e5 = new TableColumn("Entity5");


                tc_e1.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("entity1"));
                tv_queryResult.getColumns().add(tc_e1);
                tc_e2.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("entity2"));
                tv_queryResult.getColumns().add(tc_e2);
                tc_e3.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("entity3"));
                tv_queryResult.getColumns().add(tc_e3);
                tc_e4.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("entity4"));
                tv_queryResult.getColumns().add(tc_e4);
                tc_e5.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("entity5"));
                tv_queryResult.getColumns().add(tc_e5);
            }
        }

        tv_queryResult.setItems(singleResult);
    }

    public void singleQuery()
    {
        String query = tf_query.getText();
        boolean save = cb_save.isSelected();
        File resultFile = checkSave();

        if(save && resultFile == null) return;

        if(query != null && !query.isEmpty())
        {
            boolean semantic = cb_semantic.isSelected();
            boolean clickStream = cb_click.isSelected();

            l_resultInfo.setText("");
            viewModel.singleQuery(query,resultFile,semantic,clickStream);
        }
        else
        {
            // empty, show alert
            showAlert("Alert","Query is empty","query filed is empty, cannot retrieve");
        }
    }

    public File checkSave()
    {
        File resultDir = null;

        boolean save = cb_save.isSelected();
        String resultPath = tf_resultPath.getText();

        if(save)
        {
            if(resultPath != null && !resultPath.isEmpty())
            {
                resultDir = new File(resultPath);

                if(!resultDir.exists() || !resultDir.isDirectory())
                {
                    // not a directory, show alert
                    showAlert("Alert","Path is not a directory","result path is not directory");
                    return null;
                }
            }
            else
            {
                // empty, show alert
                showAlert("Alert","Path is empty","result path is empty");
            }
        }

        return resultDir;
    }

    public void multipleQueries()
    {
        String queriesPath = tf_queryPath.getText();
        File resultFile = checkSave();

        boolean save = cb_save.isSelected();
        if(save && resultFile == null) return;

        if(queriesPath != null && !queriesPath.isEmpty())
        {
            File queriesFile = new File(queriesPath);

            if(queriesFile.exists() && queriesFile.isFile())
            {
                boolean semantic = cb_semantic.isSelected();
                boolean clickStream = cb_click.isSelected();

                l_resultInfo.setText("");
                viewModel.multipleQueries(queriesFile,resultFile,semantic,clickStream);
            }
            else
            {
                // not a file, show alert
                showAlert("Alert","Path is not a file","queries path not exists");
            }
        }
        else
        {
            // empty, show alert
            showAlert("Alert","Path is empty","query or/and result paths are empty");
        }
    }

    public void browseResultPath()
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Browse for a directory to save queries result");
        File chosen = directoryChooser.showDialog(b_browsePostPath.getScene().getWindow());

        if (chosen != null)
        {
            tf_resultPath.setText(chosen.getPath());
        }
    }

    public void browseQueriesPath()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Browse for a files that contains queries information");

        File chosen = fileChooser.showOpenDialog(b_browsePostPath.getScene().getWindow());

        if (chosen != null)
        {
            tf_queryPath.setText(chosen.getPath());
        }
    }
    //</editor-fold>

}
