package Advance_Programming.Maze_Game_Part_C.src.View;

import ViewModel.MyViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This view class represents a save/load scene that the user can save/load a board from disk
 * Created by Assaf Attias
 */
public class SaveLoadController
{
    @FXML
    private MyViewModel viewModel;
    public Button b_BrowseSave, b_BrowseLoad, b_Load, b_Save;
    public TextField tf_PathDir, tf_PathFile, tf_Name;

    /**
     * Link parent's viewModel to this view to send commands
     * @param viewModel - viewModel to command
     */
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    //<editor-fold desc="Browse">
    /**
     * Open a directoryChooser to choose where to save the board file
     */
    public void browseSave()
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Browse for a directory to save");
        File chosen = directoryChooser.showDialog(b_BrowseSave.getScene().getWindow());

        if (chosen != null && chosen.isDirectory())
        {
            tf_PathDir.setText(chosen.getPath());
        }
    }

    /**
     * Open a fileChooser to choose a board file path to load
     */
    public void browseLoad()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Browse for a directory to save");
        File chosen = fileChooser.showOpenDialog(b_BrowseLoad.getScene().getWindow());

        if(chosen != null && chosen.isFile())
        {
            tf_PathFile.setText(chosen.getPath());
        }
    }
    //</editor-fold>

    //<editor-fold desc="Submit Request">
    /**
     * Check the user input request to load a board file, notify for mistakes and command model to load
     */
    public void checkAndLoad()
    {
        // validate
        if(tf_PathFile.getText().equals("")) showAlert("Wrong Input","File Path Can not be empty");
        else
        {
            File fileToLoad = new File(tf_PathFile.getText());
            if(fileToLoad == null || !fileToLoad.exists() || !fileToLoad.isFile()) showAlert("Wrong Input","File Not found");
            else
            {
                // try to load
                ((Stage)b_Load.getScene().getWindow()).close();
                viewModel.loadBoard(fileToLoad.getPath());
            }
        }
    }

    /**
     * Check the user input request to save a board file, notify for mistakes and command model to save
     */
    public void checkAndSave()
    {
        // validate
        if(tf_Name.getText().equals("")) showAlert("Wrong Input","File Name Can not be empty");
        else if (tf_PathDir.getText().equals("")) showAlert("Wrong Input","Please chose a directory path");
        else
        {
            // valid directory
            File directory = new File(tf_PathDir.getText());
            if(directory == null || !directory.isDirectory()) showAlert("Wrong Input","Wrong directory path");
            else
            {
                // check special character
                Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(tf_Name.getText());
                boolean result = matcher.find();
                if(result) showAlert("Wrong Input","Special Characters are not allowed");
                else
                {
                    // valid file
                    File fileToSave = new File(directory.getPath() + "/" + tf_Name.getText() + ".board");
                    if(fileToSave.exists()) showAlert("Wrong Input","File already exists");
                    else {
                        // save
                        ((Stage)b_Save.getScene().getWindow()).close();
                        viewModel.saveBoard(fileToSave.getPath());
                    }
                }
            }
        }
    }

    /**
     * Show an alert if any given parameters in the query is wrong to notify the user to change
     * @param alertHeader - header that indicates what is wrong
     * @param alertMessage - message to instruct the user how to change
     */
    private void showAlert(String alertHeader, String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(alertHeader);
        alert.setTitle("Alert");
        alert.setContentText(alertMessage);
        alert.show();
    }
    //</editor-fold>
}
