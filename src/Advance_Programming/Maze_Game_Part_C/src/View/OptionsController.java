package Advance_Programming.Maze_Game_Part_C.src.View;

import Server.Configurations;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This view class represents a option scene that the user can change the configurations
 * Created by Assaf Attias
 */
public class OptionsController implements Initializable
{
    @FXML
    private ToggleGroup radioGroup;
    public Button b_submit;
    public CheckBox check_music;
    public ImageView img_character, img_goal, img_wall, img_path;
    public RadioButton rb_mouse, rb_pacman;
    public ComboBox<String> combo_solveAlgorithm, combo_generateAlgorithm;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // set Music Configurations
        if(Configurations.getProperty("music").equals("1"))
        {
            check_music.setSelected(true);
        }
        else check_music.setSelected(false);

        // set algorithms Configurations
        combo_generateAlgorithm.getItems().addAll(
                "empty",
                "simple",
                "hard"
        );
        combo_generateAlgorithm.setOnAction(e->combo_generateAlgorithm.getValue());

        combo_solveAlgorithm.getItems().addAll(
                "Breadth First Search",
                "Depth First Search",
                "Best First Search"
        );

        combo_generateAlgorithm.getSelectionModel().select(Configurations.getProperty("generatorType")); // config
        combo_generateAlgorithm.setPromptText(Configurations.getProperty("generatorType")); // config
        combo_solveAlgorithm.setPromptText(Configurations.getProperty("solvingAlgorithm")); // config
        combo_solveAlgorithm.getSelectionModel().select(Configurations.getProperty("solvingAlgorithm")); // config

        // set Theme Configurations
        radioGroup = new ToggleGroup();
        rb_mouse.setToggleGroup(radioGroup);
        rb_pacman.setToggleGroup(radioGroup);

        String themeName = Configurations.getProperty("themeName");
        reDrawImages(Configurations.getProperty("characterImage"),Configurations.getProperty("goalImage"),Configurations.getProperty("wallImage"),Configurations.getProperty("pathImage"));

        if(themeName.equals("Pacman"))
        {
            radioGroup.selectToggle(rb_pacman);
        }
        else if(themeName.equals("Mouse"))
        {
            radioGroup.selectToggle(rb_mouse);
        }


        radioGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) ->
        {
            RadioButton themeButton = (RadioButton)newVal;
            String name = themeButton.getText();
            if(name.equals("Pacman"))
            {
                reDrawImages("resources/images/pacman.png","resources/images/pacmanGoal.png","resources/images/pacmanWall.png","resources/images/pacmanPath.png");
            }
            else if(name.equals("Mouse"))
            {
                reDrawImages("resources/images/mouse.png","resources/images/cheese.png","resources/images/wall.png","resources/images/path.png");
            }
            //System.out.println("Selected: " + themeButton.getText());

        });
    }

    /**
     * Redraw images to the view base on a given Images paths
     * @param character - character Image path
     * @param goal - goal Image path
     * @param wall - wall Image path
     * @param path - path Image path
     */
    private void reDrawImages(String character, String goal, String wall, String path)
    {
        img_character.setImage(new Image(new File(character).toURI().toString()));
        img_goal.setImage(new Image(new File(goal).toURI().toString()));
        img_wall.setImage(new Image(new File(wall).toURI().toString()));
        img_path.setImage(new Image(new File(path).toURI().toString()));
    }

    /**
     * Set configuration of a theme base on the radioBox selected from the group
     */
    private void setImages()
    {
        String name = ((RadioButton)radioGroup.getSelectedToggle()).getText();
        if(name.equals("Pacman"))
        {
            Configurations.setProperties("characterImage","resources/images/pacman.png");
            Configurations.setProperties("goalImage","resources/images/pacmanGoal.png");
            Configurations.setProperties("wallImage","resources/images/pacmanWall.png");
            Configurations.setProperties("pathImage","resources/images/pacmanPath.png");
            Configurations.setProperties("guideImage","resources/images/pacmanGuide.png");
        }
        else if(name.equals("Mouse"))
        {
            Configurations.setProperties("characterImage","resources/images/mouse.png");
            Configurations.setProperties("goalImage","resources/images/cheese.png");
            Configurations.setProperties("wallImage","resources/images/wall.png");
            Configurations.setProperties("pathImage","resources/images/path.png");
            Configurations.setProperties("guideImage","resources/images/crumbs.png");
        }
    }

    /**
     * Submit the current configuration the user selected and update the configuration file accordingly
     */
    public void submitConfiguration()
    {
        if(check_music.isSelected()) Configurations.setProperties("music","1");
        else Configurations.setProperties("music","0");

        Configurations.setProperties("generatorType", combo_generateAlgorithm.getValue());
        Configurations.setProperties("solvingAlgorithm",combo_solveAlgorithm.getValue());
        Configurations.setProperties("themeName",((RadioButton)radioGroup.getSelectedToggle()).getText());
        setImages();

        ((Stage)b_submit.getScene().getWindow()).close();
    }

}
