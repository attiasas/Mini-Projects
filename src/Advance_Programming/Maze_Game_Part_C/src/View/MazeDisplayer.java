package Advance_Programming.Maze_Game_Part_C.src.View;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.FileInputStream;

/**
 * This class represents Maze Displayer that can draw a given maze, character and solution dynamically
 * Created by Assaf Attias
 */
public class MazeDisplayer extends Canvas
{
    private int[][] mazeSlice;

    private int characterPositionLayer;
    private int characterPositionRow;
    private int characterPositionColumn;

    private int goalPositionLayer;
    private int goalPositionRow;
    private int goalPositionColumn;

    private int[][] solutionSlice;
    private boolean requestedSolution;

    private double zoomFactor = 1;
    public double cellSize = 0;
    private int startRow = 0;
    private int startColumn = 0;

    public StringProperty ImageFileNameWall = new SimpleStringProperty();
    public StringProperty ImageFileNamePath = new SimpleStringProperty();
    public StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    public StringProperty ImageFileNameGoal = new SimpleStringProperty();
    public StringProperty ImageFileNameGuide = new SimpleStringProperty();

    public BooleanProperty canMoveUpLayer = new SimpleBooleanProperty();
    public BooleanProperty canMoveDownLayer = new SimpleBooleanProperty();

    //<editor-fold desc="Display Requests">
    /**
     * Set the maze that will be drawn to the Display, the maze will be represented as an array[row][column]
     * @param maze int[row][column], 1-wall , 0 - path, otherwise undefined
     */
    public void setMazeSlice(int[][] maze)
    {
        mazeSlice = maze;
        redraw();
    }

    /**
     * Set the character position on the board
     * @param layer - character layer position
     * @param row - character row position
     * @param column - character column position
     */
    public void setCharacterPosition(int layer, int row, int column) {
        characterPositionLayer = layer;
        characterPositionRow = row;
        characterPositionColumn = column;
        redraw();
    }

    /**
     * Set the goal position on the board
     * @param layer - goal layer position
     * @param row - goal row position
     * @param column - goal column position
     */
    public void setGoalPosition(int layer, int row, int column)
    {
        goalPositionLayer = layer;
        goalPositionColumn = column;
        goalPositionRow = row;
        redraw();
    }

    /**
     * Setter for requested solution, if true it will display the current solution on the board
     * @param requestedSolution - true will show solution, otherwise will not show
     */
    public void setRequestedSolution(boolean requestedSolution) {
        this.requestedSolution = requestedSolution;
    }

    /**
     * Set the solution guide slice in the board in the same layer as the player
     * @param solutionSlice int[row][column], 1= part of solution, otherwise nothing
     */
    public void setSolutionSlice(int[][] solutionSlice) {
        this.solutionSlice = solutionSlice;
        redraw();
    }

    /**
     * Zoom the display closer/farther from the character
     * @param factor - factor the add/decrease from the zoom factor
     */
    public void zoom(double factor)
    {
        zoomFactor += factor;
        //if(zoomFactor >= 5) zoomFactor = 5;
        if(zoomFactor <= 1) zoomFactor = 1;
        redraw();
    }

    /**
     * Clear the current zoom to default factor ( = 1) so that the zoom is the farthest it can get
     */
    public void clearZoom()
    {
        zoomFactor = 1;
        redraw();
    }
    //</editor-fold>

    //<editor-fold desc="Mouse Translator">
    /**
     * Translate a current mouseEvent on the display board to tell what the cell row index that the mouse is on
     * @param mouseEvent - event on the display, otherwise not defined
     * @return - cell row index in board
     */
    public int translateTouchToRow(MouseEvent mouseEvent)
    {
        if(mazeSlice == null || mazeSlice[0] == null) return 0;
        return (int)(startRow + (mouseEvent.getY() / cellSize));
    }

    /**
     * Translate a current mouseEvent on the display board to tell what the cell column index that the mouse is on
     * @param mouseEvent - event on the display, otherwise not defined
     * @return - cell column index in board
     */
    public int translateTouchToColumn(MouseEvent mouseEvent)
    {
        if(mazeSlice == null || mazeSlice[0] == null) return 0;
        return (int)(startColumn + (mouseEvent.getX() / cellSize));
    }
    //</editor-fold>

    //<editor-fold desc="Getter and Setter Properties">
    public boolean isCanMoveUpLayer() {
        return canMoveUpLayer.get();
    }

    public void setCanMoveUpLayer(boolean canMoveUpLayer) {
        this.canMoveUpLayer.set(canMoveUpLayer);
    }

    public boolean isCanMoveDownLayer() {
        return canMoveDownLayer.get();
    }

    public void setCanMoveDownLayer(boolean canMoveDownLayer) {
        this.canMoveDownLayer.set(canMoveDownLayer);
    }

    public String getImageFileNameGuide() {
        return ImageFileNameGuide.get();
    }

    public void setImageFileNameGuide(String imageFileNameGuide) {
        this.ImageFileNameGuide.set(imageFileNameGuide);
    }

    public String getImageFileNameGoal() {
        return ImageFileNameGoal.get();
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.ImageFileNameGoal.set(imageFileNameGoal);
    }

    public String getImageFileNamePath() {
        return ImageFileNamePath.get();
    }

    public void setImageFileNamePath(String imageFileNamePath) {
        this.ImageFileNamePath.set(imageFileNamePath);
    }

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }
    //</editor-fold>

    /**
     * Main draw function, after any changes/updates to the parameters this function will redraw the scene
     */
    public void redraw()
    {
        if(mazeSlice != null)
        {
            // align to center and calculate cell size base on zoom and board size
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rowsToDraw = (int) (mazeSlice.length / zoomFactor);
            if(rowsToDraw <= 0) rowsToDraw = 1;
            int columnsToDraw = (int) (mazeSlice[0].length / zoomFactor);
            if(columnsToDraw <= 0) columnsToDraw = 1;
            double cellHeight = canvasHeight / rowsToDraw;
            double cellWidth = canvasWidth / columnsToDraw;

            cellSize = cellHeight;
            if(cellHeight > cellWidth) cellSize = cellWidth;

            canvasHeight = cellSize * rowsToDraw;
            canvasWidth = cellSize * columnsToDraw;

            layoutXProperty().set((getWidth()/2) - (canvasWidth/2));
            layoutYProperty().set((getHeight()/2) - (canvasHeight/2));

            try
            {
                // load image files from configurations
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image pathImage = new Image(new FileInputStream(ImageFileNamePath.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                Image goalImage = new Image(new FileInputStream(ImageFileNameGoal.get()));
                Image solutionGuideImage = new Image(new FileInputStream(ImageFileNameGuide.get()));

                // calculate which tiles to draw
                startRow = characterPositionRow - (rowsToDraw / 2);
                if(startRow < 0) startRow = 0;
                int maxRow = startRow + rowsToDraw;
                if(maxRow > mazeSlice.length)
                {
                    startRow -= (maxRow - mazeSlice.length);
                    if(startRow < 0) startRow = 0;
                    maxRow = mazeSlice.length;
                }

                startColumn = characterPositionColumn - (columnsToDraw / 2);
                if(startColumn < 0) startColumn = 0;
                int maxColumn = startColumn + columnsToDraw;
                if(maxColumn > mazeSlice[0].length)
                {
                    startColumn -= (maxColumn - mazeSlice[0].length);
                    if(startColumn < 0) startColumn = 0;
                    maxColumn = mazeSlice[0].length;
                }

                // clear previous
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                gc.setFill(Color.WHITE);
                gc.fillRect(0,0,canvasWidth,canvasHeight);

                // draw maze
                int rowCounter = 0;
                for(int row = startRow; row < maxRow; row++)
                {
                    int colcounter = 0;
                    for(int column = startColumn; column < maxColumn; column++)
                    {
                        if(mazeSlice[row][column] == 1)
                        {
                            // draw wall
                            gc.drawImage(wallImage,colcounter * cellSize, rowCounter * cellSize, cellSize, cellSize);
                        }
                        else
                        {
                            // draw path
                            gc.drawImage(pathImage,colcounter * cellSize, rowCounter * cellSize, cellSize, cellSize);

                            // draw solution
                            if(requestedSolution && solutionSlice != null && solutionSlice[row][column] == 1)
                            {
                                gc.drawImage(solutionGuideImage,colcounter * cellSize, rowCounter * cellSize, cellSize, cellSize);
                            }
                        }

                        // draw goal
                        if(characterPositionLayer == goalPositionLayer && row == goalPositionRow && column == goalPositionColumn)
                        {
                            gc.drawImage(goalImage,colcounter * cellSize,rowCounter * cellSize, cellSize, cellSize);
                        }

                        // draw character
                        if(row == characterPositionRow && column == characterPositionColumn)
                        {
                            gc.drawImage(characterImage,colcounter * cellSize,rowCounter * cellSize, cellSize, cellSize);

                            // draw arrows for layer connection
                            if(canMoveUpLayer.get())
                            {
                                Image upArrow = new Image(new FileInputStream("resources/images/upArrow.png"));
                                gc.drawImage(upArrow,colcounter * cellSize,rowCounter * cellSize, cellSize, cellSize);
                            }
                            if(canMoveDownLayer.get())
                            {
                                Image downArrow = new Image(new FileInputStream("resources/images/downArrow.png"));
                                gc.drawImage(downArrow,colcounter * cellSize,rowCounter * cellSize, cellSize, cellSize);
                            }
                        }

                        colcounter++;
                    }
                    rowCounter++;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());
        }
    }

}
