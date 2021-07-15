package Advance_Programming.Maze_Game_Part_C.src.View;

import Model.IModel;
import Server.Configurations;
import ViewModel.MyViewModel;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * This Class Represents a View in MVVM architecture that controls the GUI in the application
 * Created by Assaf Attias
 */
public class MyViewController implements Observer, Initializable,IView
{
    @FXML
    private MyViewModel viewModel;
    private boolean requestedSolution;
    private MediaPlayer mediaPlayer;

    public MenuItem b_NewBoard,b_Controls,b_About,b_Exit,b_saveBoard;
    public Label lb_layer, lb_maxLayer, lb_steps;
    public MazeDisplayer mazeDisplayer;

    public Button b_solve;
    public Pane gamePane;
    public VBox gameBox;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // bind to parent
        gamePane.prefHeightProperty().bind(gameBox.heightProperty());
        gamePane.prefWidthProperty().bind(gameBox.widthProperty());
        mazeDisplayer.widthProperty().bind(gamePane.widthProperty());
        mazeDisplayer.heightProperty().bind(gamePane.heightProperty());

        // handle size change and zoom
        mazeDisplayer.widthProperty().addListener((v, o, n)->{
            displayBoard();
            displaySolution();
            displayCharacter();
        });
        mazeDisplayer.heightProperty().addListener((v, o, n)->{
            displayBoard();
            displaySolution();
            displayCharacter();
        });
        mazeDisplayer.setOnScroll(e->{
            double zoomFactor = 0.2;
            if(e.isControlDown() && viewModel.isControlPreesed())
            {
                if (e.getDeltaY() <= 0) {
                    // zoom out
                    zoomFactor = -zoomFactor;
                }
                mazeDisplayer.zoom(zoomFactor);
            }
            e.consume();
        });

        setConfigurations();
    }

    //<editor-fold desc="View Model Observer">
    @Override
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
    }

    /**
     * Bind properties of viewModel to the views members
     * @param viewModel - view model to observe
     */
    private void bindProperties(MyViewModel viewModel)
    {
        mazeDisplayer.canMoveUpLayer.bind(viewModel.canMoveUp);
        mazeDisplayer.canMoveDownLayer.bind(viewModel.canMoveDown);

        lb_steps.textProperty().bind(viewModel.currentStepProperty());
        lb_layer.textProperty().bind(viewModel.currentLayerProperty());
        lb_maxLayer.textProperty().bind(viewModel.maxLayerNumberProperty());
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if(o == viewModel)
        {
            int code = (int)arg;
            if(code == IModel.BOARDGENERATED)
            {
                // init scene dimensions
                Stage current = (Stage)mazeDisplayer.getScene().getWindow();
                current.setMaximized(false);
                int[][] slice = viewModel.getSliceBoard();
                int cellSize = 40;

                if(cellSize * slice.length < 1000)
                {
                    current.setHeight(cellSize * slice.length);
                    if(current.getHeight() < 200) current.setHeight(200);
                }
                else current.setHeight(1000);

                if(cellSize * slice[0].length < 1000)
                {
                    current.setWidth(cellSize *  slice[0].length);
                    if(current.getWidth() < 200) current.setWidth(200);
                }
                else current.setWidth(1000);

                // init media player and background song
                if(mediaPlayer == null || mediaPlayer.getStatus() == MediaPlayer.Status.DISPOSED)
                {
                    mediaPlayer = new MediaPlayer(new Media(new File("resources/Music/backgroundMusic.mp3").toURI().toString()));
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    mediaPlayer.play();
                }
                // mute configuration
                if(Configurations.getProperty("music").equals("1")) mediaPlayer.setMute(false);
                else mediaPlayer.setMute(true);

            }

            // update Display
            if(code == IModel.BOARDGENERATED || code == IModel.MOVEDLAYER || code == IModel.RESET)
            {
                displayBoard();
            }

            displaySolution();
            displayCharacter();

            // check if game over
            if(viewModel.isGoalReached())
            {
                openVictoryStage();
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Menu Sub Stages">
    @Override
    public void openLoadStage()
    {
        try
        {
            Stage loadStage = new Stage();
            loadStage.setTitle("Load Board");
            loadStage.setResizable(false);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoadView.fxml"));
            Parent root = fxmlLoader.load();
            loadStage.setScene(new Scene(root,440,100));

            SaveLoadController controller = fxmlLoader.getController();
            controller.setViewModel(viewModel);

            loadStage.initModality(Modality.APPLICATION_MODAL);
            loadStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void openSaveStage()
    {
        try
        {
            Stage saveStage = new Stage();
            saveStage.setTitle("Save Current Board");
            saveStage.setResizable(false);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SaveView.fxml"));
            Parent root = fxmlLoader.load();
            saveStage.setScene(new Scene(root,460,140));

            SaveLoadController controller = fxmlLoader.getController();
            controller.setViewModel(viewModel);

            saveStage.initModality(Modality.APPLICATION_MODAL);
            saveStage.show();

            // alert that game was saved
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void openOptionsStage()
    {
        try
        {
            Stage optionStage = new Stage();
            optionStage.setTitle("Board Options");

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("OptionsView.fxml"));
            Parent root = fxmlLoader.load();
            optionStage.setScene(new Scene(root,369,369));

            optionStage.initModality(Modality.APPLICATION_MODAL);
            optionStage.showAndWait();

            // update screen to current coonfiguration
            setConfigurations();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void openGenerateStage()
    {
        try
        {
            Stage generateStage = new Stage();
            generateStage.setTitle("Generate New Board");
            generateStage.setResizable(false);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewBoardView.fxml"));
            Parent root = fxmlLoader.load();
            generateStage.setScene(new Scene(root,353,190));

            GenerateController controller = fxmlLoader.getController();
            controller.setViewModel(viewModel);

            generateStage.initModality(Modality.APPLICATION_MODAL);
            generateStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void openHelpStage()
    {
        Parent root = null;
        try
        {
            root = FXMLLoader.load(getClass().getResource("HelpView.fxml"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(root != null)
        {
            Stage generateStage = new Stage();
            generateStage.initModality(Modality.APPLICATION_MODAL);
            generateStage.setTitle("Game Controls");
            generateStage.setResizable(false);

            generateStage.setScene(new Scene(root,640,340));
            generateStage.showAndWait();
        }
    }

    @Override
    public void openAboutStage()
    {
        Parent root = null;
        try
        {
            root = FXMLLoader.load(getClass().getResource("AboutView.fxml"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(root != null)
        {
            Stage generateStage = new Stage();
            generateStage.initModality(Modality.APPLICATION_MODAL);
            generateStage.setTitle("About The Application");
            generateStage.setResizable(false);

            generateStage.setScene(new Scene(root,620,340));
            generateStage.showAndWait();
        }
    }

    public void openVictoryStage()
    {
        try
        {
            Stage goalStage = new Stage();
            goalStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    viewModel.reset();
                }
            });

            b_solve.setDisable(true);

            goalStage.setTitle("Board Complete");

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VictoryView.fxml"));
            Parent root = fxmlLoader.load();
            goalStage.setScene(new Scene(root));

            VictoryController controller = fxmlLoader.getController();
            controller.setParameters(this,100,viewModel.isSolutionUsed(),viewModel.getNumberOfSteps(),viewModel.getLeastSteps(),viewModel.getTimeToSolve());

            reset();

            goalStage.initModality(Modality.APPLICATION_MODAL);
            goalStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Commands">
    @Override
    public void exit()
    {
        viewModel.exit();
        ((Stage)gamePane.getScene().getWindow()).close();
    }

    @Override
    public void reset()
    {
        mazeDisplayer.clearZoom();
        if(mediaPlayer != null) mediaPlayer.dispose();
        viewModel.reset();
        b_saveBoard.setDisable(true);
        setRequestedSolution(false);
    }

    @Override
    public void KeyReleased(KeyEvent keyEvent)
    {
        viewModel.keyReleased(keyEvent.getCode());
        keyEvent.consume();
    }

    @Override
    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent)
    {
        viewModel.mousePressed(mazeDisplayer.translateTouchToRow(mouseEvent),mazeDisplayer.translateTouchToColumn(mouseEvent));
        mouseEvent.consume();
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent)
    {
        viewModel.mouseReleased(mazeDisplayer.translateTouchToRow(mouseEvent),mazeDisplayer.translateTouchToColumn(mouseEvent));
        mouseEvent.consume();
    }

    @Override
    public void mouseDrag(MouseEvent mouseEvent)
    {
        viewModel.mouseDrag(mazeDisplayer.translateTouchToRow(mouseEvent),mazeDisplayer.translateTouchToColumn(mouseEvent));
        mouseEvent.consume();
    }

    @Override
    public void requestSolution()
    {
        setRequestedSolution(!requestedSolution);
    }

    /**
     * Set a flag if the user requested to see/hide a solution and update view accordingly
     * @param b - new value to request flag
     */
    private void setRequestedSolution(boolean b)
    {
        requestedSolution = b;
        mazeDisplayer.setRequestedSolution(b);

        if(b)
        {
            // hide
            b_solve.setText("Hide Solution");
            viewModel.requestSolution();
        }
        else
        {
            // show
            b_solve.setText("Solve");
            mazeDisplayer.redraw();
        }
    }

    //</editor-fold>

    //<editor-fold desc="Display">
    @Override
    public void displayBoard()
    {
        int[][] board = viewModel.getSliceBoard();
        if(board != null)
        {
            b_solve.setDisable(false);
            b_saveBoard.setDisable(false);
        }

        mazeDisplayer.setGoalPosition(viewModel.getGoalPositionLayerIndex(), viewModel.getGoalPositionRowIndex(),viewModel.getGoalPositionColumnIndex());
        mazeDisplayer.setMazeSlice(board);
    }

    @Override
    public void displaySolution()
    {
        if(requestedSolution)
        {
            mazeDisplayer.setSolutionSlice(viewModel.getSliceSolution());
        }
    }

    @Override
    public void displayCharacter()
    {
        mazeDisplayer.setCharacterPosition(viewModel.getChatacterPositionLayer(),viewModel.getCharacterPositionRow(),viewModel.getCharacterPositionColumn());
    }

    /**
     * Set the view according to the current user configuration, after a change has been made this function update the screen and redraw
     */
    public void setConfigurations()
    {
        if(mediaPlayer != null)
        {
            if(Configurations.getProperty("music").equals("1")) mediaPlayer.setMute(false);
            else mediaPlayer.setMute(true);
        }

        mazeDisplayer.ImageFileNameCharacter.setValue(Configurations.getProperty("characterImage"));
        mazeDisplayer.ImageFileNameGoal.setValue(Configurations.getProperty("goalImage"));
        mazeDisplayer.ImageFileNameWall.setValue(Configurations.getProperty("wallImage"));
        mazeDisplayer.ImageFileNamePath.setValue(Configurations.getProperty("pathImage"));
        mazeDisplayer.ImageFileNameGuide.setValue(Configurations.getProperty("guideImage"));
        mazeDisplayer.redraw();
    }
    //</editor-fold>

}
