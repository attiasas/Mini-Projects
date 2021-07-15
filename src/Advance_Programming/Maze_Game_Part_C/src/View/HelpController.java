package Advance_Programming.Maze_Game_Part_C.src.View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This view class represents a generate scene that the user will be to generate a board
 * Created by Assaf Attias
 */
public class HelpController implements Initializable {

    @FXML
    ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        File file = new File("resources/images/KeysPad.png");
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
    }
}
