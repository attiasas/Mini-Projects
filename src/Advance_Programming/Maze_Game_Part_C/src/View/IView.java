package Advance_Programming.Maze_Game_Part_C.src.View;

import ViewModel.MyViewModel;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * This Interface defines a view implementing GUI of BoardGames as part of MVVM architecture
 * Created by Assaf Attias
 */
public interface IView
{
    /**
     * Inject ViemModel
     * @param viewModel
     */
    void setViewModel(MyViewModel viewModel);

    //<editor-fold desc="Display Board">
    /**
     * Display board in the application
     */
    void displayBoard();

    /**
     * Display solution to the board in the application
     */
    void displaySolution();

    /**
     * Display the character on the board in the application
     */
    void displayCharacter();
    //</editor-fold>

    //<editor-fold desc="Menu Sub Stages">
    /**
     * Open load SubStage to load a board to display
     */
    void openLoadStage();

    /**
     * Open save SubStage to save the current board that is displaying
     */
    void openSaveStage();

    /**
     * Open option SubStage to show and set the configurations of the application
     */
    void openOptionsStage();

    /**
     * Open generate SubStage to generate new board base on given parameters
     */
    void openGenerateStage();

    /**
     * Open help SubStage showing explanation to the rules of the board
     */
    void openHelpStage();

    /**
     * Open about SubStage showing information of the application
     */
    void openAboutStage();

    /**
     * Open victory subStage when goal is achieved
     */
    void openVictoryStage();
    //</editor-fold>

    //<editor-fold desc="Request Commands">
    /**
     * Request the view model to forward a command that a key was released
     * @param keyEvent - when releasing a key
     */
    void KeyReleased(KeyEvent keyEvent);

    /**
     * Request the view model to forward a command that a key was Pressed
     * @param keyEvent - when Pressing a key
     */
    void KeyPressed(KeyEvent keyEvent);

    void mousePressed(MouseEvent mouseEvent);

    void mouseReleased(MouseEvent mouseEvent);

    void mouseDrag(MouseEvent mouseEvent);

    /**
     * Request the view model to forward a command to exit the application
     */
    void exit();

    /**
     * Request model to reset his state to initialize values and delete the current board
     */
    void reset();

    /**
     * Request model to generate a solution to its current board
     */
    void requestSolution();
    //</editor-fold>
}
