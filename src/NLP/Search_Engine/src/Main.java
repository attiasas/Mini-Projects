package NLP.Search_Engine.src;

import Model.MyModel;

import Model.SearchEngine.Queries.Searcher;
import View.View;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        ClassLoader classLoader = new Main().getClass().getClassLoader();

        // --------------------------------------
        primaryStage.setTitle("Search Engine");
        // --------------------------------------

        // -- Connect Model to ViewModel ---------
        MyModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        // --------------------------------------
        // -- Load fxml and set scene -----------
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("View/GUI.fxml"));
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        // --------------------------------------
        // -- Connect View to ViewModel ---------
        View view = fxmlLoader.getController();
        view .setViewModel(viewModel);
        viewModel.addObserver(view);
        // --------------------------------------

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
