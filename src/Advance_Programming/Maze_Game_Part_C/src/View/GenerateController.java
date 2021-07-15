package Advance_Programming.Maze_Game_Part_C.src.View;

import ViewModel.MyViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * This view class represents a generate scene that the user will be to generate a board
 * Created by Assaf Attias
 */
public class GenerateController
{
    @FXML
    private MyViewModel viewModel;
    public TextField tf_Row,tf_Column,tf_Layer;
    public CheckBox cb_3D;
    public Label layerText;
    public Button b_Generate;

    /**
     * Link parent's viewModel to this view to send commands
     * @param viewModel - viewModel to command
     */
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    //<editor-fold desc="Check Input">
    /**
     * Check input in Row textfield to set the correctStyle
     */
    public void checkRow()
    {
        setStyle(tf_Row);
    }

    /**
     * Check input in Column textfield to set the correctStyle
     */
    public void checkColumn()
    {
        setStyle(tf_Column);
    }

    /**
     * Check input in Layer textfield to set the correctStyle
     */
    public void checkLayer()
    {
        setStyle(tf_Layer);
    }

    /**
     * Checking if a given string is an integer value that is grater than 1
     * @param txt - string to check
     * @return true if the text indicate an integer (>1), false otherwise
     */
    private boolean checkInput(String txt)
    {
        boolean result = true;

        try {
            int input = Integer.parseInt(txt);
            if(input <= 1) result = false;
        }
        catch (NumberFormatException e)
        {
            result = false;
        }

        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Set View Style">
    /**
     * disable/able multiple layers mode in the view base on the checkbox
     */
    public void set3DMode()
    {
        if(cb_3D.isSelected())
        {
            layerText.setDisable(false);
            tf_Layer.setDisable(false);
        }
        else
        {
            layerText.setDisable(true);
            tf_Layer.setText("");
            setStyle(tf_Layer);
            tf_Layer.setDisable(true);
        }
    }

    /**
     * Set a color style base on a given textField input
     * @param field - textField to check
     */
    private void setStyle(TextField field)
    {
        String txt = field.getText();
        if(txt == null || txt.equals(""))
        {
            field.getStyleClass().clear();
            field.getStyleClass().add("text-field");
        }
        else if(checkInput(txt))
        {
            field.getStyleClass().clear();
            field.getStyleClass().add("text-field-ok");
        }
        else
        {
            field.getStyleClass().clear();
            field.getStyleClass().add("text-field-wrong");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Submit">
    /**
     * Sumbit a generate request base on a given parameters, check if the parameters are corrent and notify user to correct if needed
     * and than commands the model to generate a board
     */
    public void submitRequest()
    {
        // validate
        if(!checkInput(tf_Row.getText())) showAlert("Wrong Input","Number of Rows has to be an integer in the rage: [2,infinity]");
        else if(!checkInput(tf_Column.getText())) showAlert("Wrong Input","Number of Columns has to be an integer in the rage: [2,infinity]");
        else if(cb_3D.isSelected() && !checkInput(tf_Layer.getText())) showAlert("Wrong Input","Number of Layers has to be an integer in the rage: [2,infinity]");
        else
        {
            // request
            int row = Integer.valueOf(tf_Row.getText());
            int column = Integer.valueOf(tf_Column.getText());
            int layers = 1;
            if(!tf_Layer.getText().equals("")) layers = Integer.valueOf(tf_Layer.getText());

            ((Stage)b_Generate.getScene().getWindow()).close();
            viewModel.generateBoard(layers,row,column);
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
