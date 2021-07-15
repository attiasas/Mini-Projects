package Advance_Programming.Maze_Game_Part_C.src.View;

import Server.Configurations;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This view class represents a victory scene that will be shown when goal is reached
 * Created by Assaf Attias
 */
public class VictoryController implements Initializable
{
    @FXML
    private MyViewController controller;
    private MediaPlayer mediaPlayer;
    public Label lb_steps,lb_seconds,lb_bestSteps,lb_formula,lb_score;
    public Button b_newBoard, b_exit;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // init victory music
        mediaPlayer = new MediaPlayer(new Media(new File("resources/Music/winMusic.mp3").toURI().toString()));
        mediaPlayer.setStopTime(Duration.seconds(5));
        mediaPlayer.play();

        if(Configurations.getProperty("music").equals("1")) mediaPlayer.setMute(false);
        else mediaPlayer.setMute(true);
    }

    /**
     * Set Game parameters to show the user the result (information) regarding the board he played
     * @param controller - main view controller to acsses viewmodel to pass commands to the model
     * @param maxPoint - max score point that the user can get to process the result
     * @param solutionUsed - boolean to indicate if a solution used on the board
     * @param steps - number of steps the user moved
     * @param leastSteps - least number of steps require for the board
     * @param time - time passed since generating the board
     */
    public void setParameters(MyViewController controller, int maxPoint, boolean solutionUsed, int steps, int leastSteps, long time)
    {
        this.controller = controller;

        lb_steps.setText("" + steps);
        lb_bestSteps.setText("" + leastSteps);
        lb_seconds.setText("" + (time / 1000));

        int score = maxPoint - (steps - leastSteps);
        if(solutionUsed)
        {
            score -= (maxPoint / 2);
            lb_formula.setText("" + maxPoint + " - " + (steps - leastSteps) + "(Spare Steps) - " + (maxPoint / 2) + "(Solution Used)");
        }
        else
        {
            lb_formula.setText("" + maxPoint + " - " + (steps - leastSteps) + "(Spare Steps)");
        }
        if(score < 0) score = 0;

        lb_score.setText("" + score);
    }

    /**
     * Request to generate new board from the model
     */
    public void newBoard()
    {
        ((Stage)b_exit.getScene().getWindow()).close();
        controller.openGenerateStage();
    }

    /**
     * Request to end the program from model
     */
    public void exitProgram()
    {
        ((Stage)b_exit.getScene().getWindow()).close();
        controller.exit();
    }
}
