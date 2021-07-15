package Advance_Programming.Maze_Game_Part_C.src.ViewModel;

import Model.IModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;

import java.util.Observable;
import java.util.Observer;

/**
 * This Class Represents a ViewModel in MVVM architecture that links a logic model with GUI view
 * Created by Assaf Attias
 */
public class MyViewModel extends Observable implements Observer
{
    private IModel model;

    private StringProperty currentLayer = new SimpleStringProperty("0");
    private StringProperty maxLayerNumber = new SimpleStringProperty("0");
    private StringProperty currentStep = new SimpleStringProperty("0");

    private int characterPositionLayerIndex;
    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;

    private int goalPositionLayerIndex;
    private int goalPositionRowIndex;
    private int goalPositionColumnIndex;

    public BooleanProperty canMoveUp = new SimpleBooleanProperty();
    public BooleanProperty canMoveDown = new SimpleBooleanProperty();

    /**
     * dynamic Injection for model to listen
     * @param model
     */
    public MyViewModel(IModel model){
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if (o==model){
            characterPositionLayerIndex = (model.getCharacterLayerPosition());
            characterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionRowIndex = model.getCharacterPositionRow();

            canMoveUp.setValue(model.canMoveLayerUp());
            canMoveDown.setValue(model.canMoveLayerDown());

            goalPositionLayerIndex = model.getGoalLayerPosition();
            goalPositionColumnIndex = model.getGoalColumnPosition();
            goalPositionRowIndex = model.getGoalRowPosition();

            currentLayer.setValue((characterPositionLayerIndex + 1) + "");
            maxLayerNumber.setValue(model.getNumberOfLayers() + "");
            currentStep.setValue(model.getNumberOfSteps() + "");

            setChanged();
            notifyObservers(arg);
        }
    }


    //<editor-fold desc="Commands For The Model">
    /**
     * Exit program and close all operations in model
     */
    public void exit() {model.stopServers();}

    /**
     * Reset model state
     */
    public void reset() {model.reset();}

    /**
     * request model to generate solution to the board
     */
    public void requestSolution()
    {
        model.generateSolution();
    }

    /**
     * request model to load board file from a given path
     * @param pathName
     */
    public void loadBoard(String pathName)
    {
        model.loadBoard(pathName);
    }

    /**
     * request model to save the current state to a file in a given path (with name include)
     * @param pathName - full path to the file including its name (with .board extension)
     */
    public void saveBoard(String pathName)
    {
        model.saveBoard(pathName);
    }

    /**
     * request model to generate new board and update it state
     * @param layers - number of layers of the board
     * @param rows - number of rows of the board
     * @param columns - number of columns of the board
     */
    public void generateBoard(int layers, int rows, int columns){
        model.generateBoard(layers, rows, columns);
    }

    /**
     * request model to move character base on a given keycode
     * @param movement - keycode representing the key that was pressed
     */
    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public void mousePressed(int row, int column) { model.mousePressed(row,column); }

    public void mouseDrag(int row, int column) { model.mouseDrag(row,column); }

    public void mouseReleased(int row, int column) { model.mouseReleased(row,column); }

    /**
     * Notify model that a key was released for its logic
     * @param code - keycode that represents the key that was released
     */
    public void keyReleased(KeyCode code) { model.keyReleased(code); }
    //</editor-fold>

    //<editor-fold desc="Getters for information from model">
    /**
     * Fetch from model the number of steps the character moved
     * @return - number of steps since board generated
     */
    public int getNumberOfSteps() { return model.getNumberOfSteps(); }

    /**
     * Fetch from model the least number of steps required to solve the current board
     * @return - least number of steps to solve the board
     */
    public int getLeastSteps() { return model.getLeastSteps(); }

    /**
     * Fetch from model the time that passed since the current board was generated
     * @return - time that passed in miliseconds
     */
    public long getTimeToSolve() { return model.getTimeToSolve(); }

    /**
     * Fetch from model the current layer slice of the board base on the character position
     * @return - slice array that represents the board, 1 = wall, 0 = path
     */
    public int[][] getSliceBoard() {
        return model.getSliceBoard();
    }

    /**
     * Fetch from model the current solution layer slice of the board base on the character position
     * @return - slice array that represents the board, 1 = part of solution, 0 otherwise
     */
    public int[][] getSliceSolution() { return model.getSliceSolution(); }

    /**
     * Fetch from model the current layer that the character is in the board
     * @return - layer index in the board array
     */
    public int getChatacterPositionLayer() { return characterPositionLayerIndex; }

    /**
     * Fetch from model the current row that the character is in the board
     * @return - row index in the board array
     */
    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    /**
     * Fetch from model the current column that the character is in the board
     * @return - column index in the board array
     */
    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    /**
     * Fetch from model the layer that the goal is in the board
     * @return - layer index in the board array
     */
    public int getGoalPositionLayerIndex() { return goalPositionLayerIndex; }

    /**
     * Fetch from model the row that the goal is in the board
     * @return - row index in the board array
     */
    public int getGoalPositionRowIndex() {
        return goalPositionRowIndex;
    }

    /**
     * Fetch from model the column that the goal is in the board
     * @return - column index in the board array
     */
    public int getGoalPositionColumnIndex() {
        return goalPositionColumnIndex;
    }
    //</editor-fold>

    //<editor-fold desc="logic Information from model">
    /**
     * request the model to check if the character reached the goal
     * @return - true if reached, false otherwise
     */
    public boolean isGoalReached()
    {
        return model.goalReached();
    }

    /**
     * request the model to check if a solution was requested since the board was generated
     * @return - true if requested, false otherwise
     */
    public boolean isSolutionUsed() { return model.solutionUsed(); }

    /**
     * request the model to check if control key is pressed base on it state
     * @return - true if pressed, false otherwise
     */
    public boolean isControlPreesed() { return model.isControlPressed(); }
    //</editor-fold>

    //<editor-fold desc="Properties Getters And Setters">
    public String getMaxLayerNumber() {
        return maxLayerNumber.get();
    }

    public StringProperty maxLayerNumberProperty() {
        return maxLayerNumber;
    }

    public void setMaxLayerNumber(String maxLayerNumber) {
        this.maxLayerNumber.set(maxLayerNumber);
    }

    public String getCurrentStep() {
        return currentStep.get();
    }

    public StringProperty currentStepProperty() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep.set(currentStep);
    }

    public String getCurrentLayer() {
        return currentLayer.get();
    }

    public StringProperty currentLayerProperty() {
        return currentLayer;
    }

    public void setCurrentLayer(String currentLayer) {
        this.currentLayer.set(currentLayer);
    }
    //</editor-fold>
}
