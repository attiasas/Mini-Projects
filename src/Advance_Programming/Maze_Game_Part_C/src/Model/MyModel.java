package Advance_Programming.Maze_Game_Part_C.src.Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.*;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Observable;

/**
 * This class represents a model to the GUI logic of Maze Game
 * Created by Assaf Attias
 */
public class MyModel extends Observable implements IModel
{
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    private Maze maze;
    private Solution currentSolution;

    private int characterLayerPosition;
    private int characterRowPosition;
    private int characterColumnPosition;

    private int goalLayerPosition;
    private int goalRowPosition;
    private int goalColumnPosition;

    private boolean controlPress;
    private boolean requestedSolution;
    private int steps;
    private long startTimeStamp;

    private boolean draging;

    //<editor-fold desc="Servers">
    @Override
    public void startServers()
    {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    @Override
    public void stopServers()
    {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }
    //</editor-fold>

    //<editor-fold desc="Board Functions">
    @Override
    public void generateBoard(int layers, int rows, int columns)
    {
        try
        {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try
                    {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();

                        int[] mazeDimensions = new int[]{layers, rows, columns};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();

                        int arraySize = fromServer.readInt();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server

                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[arraySize];
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            client.communicateWithServer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        retry();
    }

    @Override
    public void saveBoard(String path)
    {
        if(maze == null) return;

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path)))
        {
            out.writeObject(maze);
            out.writeInt(characterLayerPosition);
            out.writeInt(characterRowPosition);
            out.writeInt(characterColumnPosition);
            out.writeBoolean(requestedSolution);
            out.writeInt(steps);
            out.writeLong((System.currentTimeMillis() - startTimeStamp));
            out.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void loadBoard(String path)
    {
        if(!path.endsWith(".board")) return;

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(path)))
        {
            Object tryRead = in.readObject();
            if(tryRead instanceof Maze)
            {
                reset();
                maze = (Maze) tryRead;
                characterLayerPosition = in.readInt();
                characterRowPosition = in.readInt();
                characterColumnPosition = in.readInt();
                requestedSolution = in.readBoolean();
                steps = in.readInt();
                startTimeStamp = System.currentTimeMillis() - in.readLong();

                goalColumnPosition = maze.getGoalPosition().getColumnIndex();
                goalRowPosition = maze.getGoalPosition().getRowIndex();
                goalLayerPosition = maze.getGoalPosition().getLayerIndex();

                setChanged();
                notifyObservers(IModel.BOARDGENERATED);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void generateSolution()
    {
        if(maze == null) return;
        requestedSolution = true;

        Maze mazeToSolve = new Maze(new Position(characterLayerPosition,characterRowPosition,characterColumnPosition),maze.getGoalPosition(),maze.getMaze());

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer)
                {
                    try
                    {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();

                        toServer.writeObject(mazeToSolve); //send maze to server
                        toServer.flush();
                        currentSolution = (Solution) fromServer.readObject();

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            client.communicateWithServer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        setChanged();
        notifyObservers(SOLUTIONGENERETED);
    }

    @Override
    public void keyReleased(KeyCode code) {
        if(code == KeyCode.CONTROL) controlPress = false;
    }

    @Override
    public void mousePressed(int row, int column)
    {
        if(maze == null) return;
        if(row == characterRowPosition && column == characterColumnPosition) draging = true;
    }

    @Override
    public void mouseDrag(int row, int column) {
        if(maze == null || !draging) return;

        if(maze.isBelongToMaze(characterLayerPosition,row,column))
        {
            boolean left = characterRowPosition == row && characterColumnPosition == (column - 1);
            boolean right = characterRowPosition == row && characterColumnPosition == (column + 1);
            boolean up = characterRowPosition == (row - 1) && characterColumnPosition == column;
            boolean down = characterRowPosition == (row + 1) && characterColumnPosition == column;
            boolean leftUp = characterRowPosition == (row - 1) && characterColumnPosition == (column - 1) && (maze.isBelongToMaze(characterLayerPosition,row-1,column) || maze.isBelongToMaze(characterLayerPosition,row,column-1));
            boolean leftDown = characterRowPosition == (row + 1) && characterColumnPosition == (column - 1) && (maze.isBelongToMaze(characterLayerPosition,row+1,column) || maze.isBelongToMaze(characterLayerPosition,row,column-1));
            boolean rightUp = characterRowPosition == (row - 1) && characterColumnPosition == (column + 1) && (maze.isBelongToMaze(characterLayerPosition,row-1,column) || maze.isBelongToMaze(characterLayerPosition,row,column+1));
            boolean rightDown = characterRowPosition == (row + 1) && characterColumnPosition == (column + 1) && (maze.isBelongToMaze(characterLayerPosition,row+1,column) || maze.isBelongToMaze(characterLayerPosition,row,column+1));

            if(left || right || up || down || leftUp || leftDown || rightUp || rightDown)
            {
                characterRowPosition = row;
                characterColumnPosition = column;
                steps++;

                setChanged();
                notifyObservers(MOVEDLAYER);
            }
        }
    }

    @Override
    public void mouseReleased(int row, int column) {
        draging = false;
    }

    @Override
    public void moveCharacter(KeyCode movement)
    {
        if(maze == null) return;
        if(goalReached()) return;

        if(movement == KeyCode.CONTROL) controlPress = true;

        switch (movement)
        {
            case NUMPAD8:
                if(maze.isBelongToMaze(characterLayerPosition,characterRowPosition-1,characterColumnPosition))
                {
                    characterRowPosition--;
                    steps++;
                    setChanged();
                    notifyObservers(CHARACTERMOVED);
                }
                break;
            case NUMPAD2:
                if(maze.isBelongToMaze(characterLayerPosition,characterRowPosition+1,characterColumnPosition))
                {
                    characterRowPosition++;
                    steps++;
                    setChanged();
                    notifyObservers(CHARACTERMOVED);
                }
                break;
            case NUMPAD4:
                if(maze.isBelongToMaze(characterLayerPosition,characterRowPosition,characterColumnPosition-1))
                {
                    characterColumnPosition--;
                    steps++;
                    setChanged();
                    notifyObservers(CHARACTERMOVED);
                }
                break;
            case NUMPAD6:
                if(maze.isBelongToMaze(characterLayerPosition,characterRowPosition,characterColumnPosition+1))
                {
                    characterColumnPosition++;
                    steps++;
                    setChanged();
                    notifyObservers(CHARACTERMOVED);
                }
                break;
            case NUMPAD9:
                if(maze.isBelongToMaze(characterLayerPosition,characterRowPosition-1,characterColumnPosition+1) && (maze.isBelongToMaze(characterLayerPosition,characterRowPosition-1,characterColumnPosition) || maze.isBelongToMaze(characterLayerPosition,characterRowPosition,characterColumnPosition+1)))
                {
                    characterColumnPosition++;
                    characterRowPosition--;
                    steps++;
                    setChanged();
                    notifyObservers(CHARACTERMOVED);
                }
                break;
            case NUMPAD7:
                if(maze.isBelongToMaze(characterLayerPosition,characterRowPosition-1,characterColumnPosition-1) && (maze.isBelongToMaze(characterLayerPosition,characterRowPosition-1,characterColumnPosition) || maze.isBelongToMaze(characterLayerPosition,characterRowPosition,characterColumnPosition-1)))
                {
                    characterColumnPosition--;
                    characterRowPosition--;
                    steps++;
                    setChanged();
                    notifyObservers(CHARACTERMOVED);
                }
                break;
            case NUMPAD3:
                if(maze.isBelongToMaze(characterLayerPosition,characterRowPosition+1,characterColumnPosition+1) && (maze.isBelongToMaze(characterLayerPosition,characterRowPosition+1,characterColumnPosition) || maze.isBelongToMaze(characterLayerPosition,characterRowPosition,characterColumnPosition+1)))
                {
                    characterColumnPosition++;
                    characterRowPosition++;
                    steps++;
                    setChanged();
                    notifyObservers(CHARACTERMOVED);
                }
                break;
            case NUMPAD1:
                if(maze.isBelongToMaze(characterLayerPosition,characterRowPosition+1,characterColumnPosition-1) && (maze.isBelongToMaze(characterLayerPosition,characterRowPosition+1,characterColumnPosition) || maze.isBelongToMaze(characterLayerPosition,characterRowPosition,characterColumnPosition-1)))
                {
                    characterColumnPosition--;
                    characterRowPosition++;
                    steps++;
                    setChanged();
                    notifyObservers(CHARACTERMOVED);
                }
                break;
            case PAGE_UP:
                if(canMoveLayerUp())
                {
                    characterLayerPosition++;
                    steps++;
                    draging = false;
                    setChanged();
                    notifyObservers(MOVEDLAYER);
                }
                break;
            case PAGE_DOWN:
                if(canMoveLayerDown())
                {
                    characterLayerPosition--;
                    steps++;
                    draging = false;
                    setChanged();
                    notifyObservers(MOVEDLAYER);
                }
                break;
        }

        if(requestedSolution) generateSolution();
    }

    @Override
    public void reset()
    {
        maze = null;
        requestedSolution = false;
        currentSolution = null;
        steps = 0;
        characterLayerPosition = -1;

        setChanged();
        notifyObservers(RESET);
    }

    /**
     * reset the current board to default position
     */
    private void retry()
    {
        if(maze == null) return;

        // reset parameters
        characterLayerPosition = maze.getStartPosition().getLayerIndex();
        characterRowPosition = maze.getStartPosition().getRowIndex();
        characterColumnPosition = maze.getStartPosition().getColumnIndex();

        goalLayerPosition = maze.getGoalPosition().getLayerIndex();
        goalColumnPosition = maze.getGoalPosition().getColumnIndex();
        goalRowPosition = maze.getGoalPosition().getRowIndex();

        requestedSolution = false;

        // reset time and steps
        startTimeStamp = System.currentTimeMillis();
        steps = 0;

        setChanged();
        notifyObservers(BOARDGENERATED);
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    @Override
    public int[][] getSliceBoard()
    {
        if(maze != null)
        {
            return maze.getMaze()[characterLayerPosition];
        }

        return null;
    }

    @Override
    public int[][] getSliceSolution()
    {
        if(maze == null || !requestedSolution) return null;

        int[][][] mazeArray = maze.getMaze();
        int[][] result = new int[mazeArray[characterLayerPosition].length][mazeArray[characterLayerPosition][0].length];

        ArrayList<AState> path = currentSolution.getSolutionPath();
        for(int i = 0; i < path.size(); i++)
        {
            if(i == 0 || i == path.size() - 1) continue; // not in character or goal position
            MazeState state = (MazeState) path.get(i);

            if(state.getState().getLayerIndex() == characterLayerPosition)
            {
                result[state.getState().getRowIndex()][state.getState().getColumnIndex()] = 1;
            }
        }

        return result;

    }

    @Override
    public int getNumberOfSteps() {
        return steps;
    }

    @Override
    public int getLeastSteps()
    {
        ISearchable searchable = new SearchableMaze(maze);
        ISearchingAlgorithm searchingAlgorithm = new BestFirstSearch();
        Solution solution = searchingAlgorithm.solve(searchable);

        return solution.getSolutionPath().size() - 1;
    }

    @Override
    public long getTimeToSolve() {
        return System.currentTimeMillis() - startTimeStamp;
    }

    @Override
    public int getCharacterPositionRow() {
        return characterRowPosition;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterColumnPosition;
    }

    @Override
    public int getCharacterLayerPosition() {
        return characterLayerPosition;
    }

    @Override
    public int getNumberOfLayers()
    {
        if(maze == null) return 0;
        return maze.getMaze().length;
    }

    @Override
    public int getGoalLayerPosition()
    {
        return goalLayerPosition;
    }

    @Override
    public int getGoalRowPosition() {
        return goalRowPosition;
    }

    @Override
    public int getGoalColumnPosition() {
        return goalColumnPosition;
    }
    //</editor-fold>

    //<editor-fold desc="Information Logic of Board">
    @Override
    public boolean isControlPressed() {
        return controlPress;
    }

    @Override
    public boolean solutionUsed()
    {
        return requestedSolution;
    }

    @Override
    public boolean canMoveLayerUp()
    {
        if(maze == null) return false;
        return maze.isBelongToMaze(characterLayerPosition + 1,characterRowPosition,characterColumnPosition);
    }

    @Override
    public boolean canMoveLayerDown()
    {
        if(maze == null) return false;
        return maze.isBelongToMaze(characterLayerPosition - 1,characterRowPosition,characterColumnPosition);
    }

    @Override
    public boolean goalReached()
    {
        if(maze == null) return false;
        return maze.getGoalPosition().equals(characterLayerPosition,characterRowPosition,characterColumnPosition);
    }
    //</editor-fold>

}
