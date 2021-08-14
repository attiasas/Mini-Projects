package NLP.Search_Engine.src.View;

import ViewModel.MyViewModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Created By: Assaf Attias
 * On 10/01/2020
 * Description:
 */
public class QueryView
{
    @FXML
    public TableView<MyViewModel.QueryResultModel> tv_queryResult;

    /**
     * Set the info of the result
     * @param results - results to show
     * @param entities - should show top entities
     */
    public void setParameters(ObservableList<MyViewModel.QueryResultModel> results, boolean entities)
    {
        TableColumn tc_docRank = new TableColumn("#");
        tv_queryResult.getColumns().add(tc_docRank);
        TableColumn tc_docId = new TableColumn("ID");
        tv_queryResult.getColumns().add(tc_docId);

        tc_docRank.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,Integer>("rank"));
        tc_docId.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("docId"));

        if(entities)
        {
            TableColumn tc_e1 = new TableColumn("Entity1");
            tc_e1.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("entity1"));
            tv_queryResult.getColumns().add(tc_e1);

            TableColumn tc_e2 = new TableColumn("Entity2");
            tc_e2.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("entity2"));
            tv_queryResult.getColumns().add(tc_e2);

            TableColumn tc_e3 = new TableColumn("Entity3");
            tc_e3.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("entity3"));
            tv_queryResult.getColumns().add(tc_e3);

            TableColumn tc_e4 = new TableColumn("Entity4");
            tc_e4.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("entity4"));
            tv_queryResult.getColumns().add(tc_e4);

            TableColumn tc_e5 = new TableColumn("Entity5");
            tc_e5.setCellValueFactory(new PropertyValueFactory<MyViewModel.QueryResultModel,String>("entity5"));
            tv_queryResult.getColumns().add(tc_e5);
        }

        tv_queryResult.setItems(results);
    }
}
