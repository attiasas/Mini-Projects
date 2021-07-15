package Advance_Programming.Maze_Game_Part_C.src.Model;

import javafx.scene.input.KeyCode;

/**
 * This Interface defines a model to the GUI logic of BoardGames as part of MVVM architecture
 * Created by Assaf Attias
 */
public interface IModel
{
    //<editor-fold desc="Servers">
    /**
     * Starts Servers to generate and solve Searchable Boards
     */
    void startServers();

    /**
     * Stops servers when closing the model
     */
    void stopServers();
    //</editor-fold>

    //<editor-fold desc="Code definitions for updating observers">
    public static int BOARDGENERATED = 0;
    public static int MOVEDLAYER = 1;
    public static int CHARACTERMOVED = 2;
    public static int SOLUTIONGENERETED = 3;
    public static int RESET = 4;
    //</editor-fold>

    //<editor-fold desc="Board Functions">
    /**
     * Request of model to generate new board for the
     * @param layers - number of layers in the board
     * @param rows - number of rows in the board
     * @param columns - number of columns in the board
     */
    void generateBoard(int layers, int rows, int columns);

    /**
     * Save the current board to a given path, if board not generated the function will not do anything
     * @param path - path to save Board file including the name of the file, extension must be .board
     */
    void saveBoard(String path);

    /**
     * Reading .board file that was saved with saveBoard function
     * @param path - path of board file
     */
    void loadBoard(String path);

    /**
     * Reset model to default state with no board
     */
    void reset();

    /**
     * Generate solution to the current board in respect to the character position
     */
    void generateSolution();

    /**
     * Board function when key released
     * @param code - keycode representing a key released
     */
    void keyReleased(KeyCode code);

    void mousePressed(int row, int column);

    void mouseDrag(int row, int column);

    void mouseReleased(int row, int column);

    /**
     * Move the character in the board base on a given keycode
     * @param movement - keycode representing a key pressed
     */
    void moveCharacter(KeyCode movement);
    //</editor-fold>

    //<editor-fold desc="Getters for information">
    /**
     * Getter for the goal layer index position in the board array
     * @return - layer index in board array
     */

    int getGoalLayerPosition();
    /**
     * Getter for the goal row index position in the board array
     * @return - row index in board array
     */
    int getGoalRowPosition();

    /**
     * Getter for the goal column index position in the board array
     * @return - column index in board array
     */
    int getGoalColumnPosition();

    /**
     * Getter for the number of steps the character moved since generating the board
     * @return - number of steps the character took
     */
    int getNumberOfSteps();

    /**
     * Getter for the least number of steps require for the current board
     * @return - least number of steps
     */
    int getLeastSteps();

    /**
     * Getter for the time that passed since generating the board in miliseconds
     * @return - time in mili seconds
     */
    long getTimeToSolve();

    /**
     * Getter for the current layer solution slice of the board base on the character layer position
     * @return - int[][] representing where a state in the solution is positioned, 1 - part of solution, 0 - otherwise
     */
    int[][] getSliceSolution();

    /**
     * Getter for the current layer slice of the board base on the character layer position
     * @return - int[][] representing the layer slice of the board, 1 - wall, 0 - path
     */
    int[][] getSliceBoard();

    /**
     * Getter for the number of layers in the current board
     * @return - the number of layers in the board
     */
    int getNumberOfLayers();

    /**
     * Getter for the character current layer index position in the board array
     * @return - layer index in board array
     */
    int getCharacterLayerPosition();

    /**
     * Getter for the character current row index position in the board array
     * @return - row index in board array
     */
    int getCharacterPositionRow();

    /**
     * Getter for the character current column index position in the board array
     * @return - column index in board array
     */
    int getCharacterPositionColumn();
    //</editor-fold>

    //<editor-fold desc="Information Logic of Board">
    /**
     * check if the character can move up a layer base on the character position
     * @return true if can move, false otherwise
     */
    boolean canMoveLayerUp();

    /**
     * check if the character can move down a layer base on the character position
     * @return true if can move, false otherwise
     */
    boolean canMoveLayerDown();

    /**
     * check if the character reached the goal in the board
     * @return - true if reached, false otherwise
     */
    boolean goalReached();

    /**
     * check if a solution was requested to the current board
     * @return - true if a request have been made, false otherwise
     */
    boolean solutionUsed();

    /**
     * Getter that tells if control key is pressed
     * @return
     */
    boolean isControlPressed();
    //</editor-fold>

}
