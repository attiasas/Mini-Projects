package Advance_Programming.Maze_Game_Part_A.algorithms.search;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;

import java.util.ArrayList;

/**
 * this class represents an adapter from maze to an implemented searchable maze problem
 * Created by Assaf Attias
 */
public class SearchableMaze implements ISearchable
{
    private Maze maze;
    public boolean includeDiagonal;

    /**
     * Constructor
     * @param maze - maze to search from
     */
    public SearchableMaze(Maze maze)
    {
        this.maze = maze;
        includeDiagonal = true;

    }

    @Override
    public AState getStartState() { return  new MazeState(maze.getStartPosition()); }

    @Override
    public AState getGoalState() { return new MazeState(maze.getGoalPosition()); }

    @Override
    public ArrayList<AState> getAllPossibleStates(AState state)
    {
        // validate
        if (!(state instanceof MazeState)) return null;

        // init
        MazeState mazeState = (MazeState)state;
        int stateRow = mazeState.getState().getRowIndex();
        int stateColumn = mazeState.getState().getColumnIndex();
        MazeState top = null;
        MazeState right = null;
        MazeState bottom = null;
        MazeState left = null;

        ArrayList<AState> successors = new ArrayList<>();

        // top
        if(maze.isBelongToMaze(stateRow - 1,stateColumn) && !(mazeState.getPrecursor() != null &&((MazeState)mazeState.getPrecursor()).equal(stateRow - 1,stateColumn)))
        {
            top = new MazeState(new Position(stateRow - 1,stateColumn),10,mazeState);
            successors.add(top);
        }
        // right
        if(maze.isBelongToMaze(stateRow,stateColumn + 1) && !(mazeState.getPrecursor() != null &&((MazeState)mazeState.getPrecursor()).equal(stateRow,stateColumn + 1)))
        {
            right = new MazeState(new Position(stateRow,stateColumn + 1),10,mazeState);
            successors.add(right);
        }
        // bottom
        if(maze.isBelongToMaze(stateRow + 1,stateColumn) && !(mazeState.getPrecursor() != null &&((MazeState)mazeState.getPrecursor()).equal(stateRow + 1,stateColumn)))
        {
            bottom = new MazeState(new Position(stateRow + 1,stateColumn),10,mazeState);
            successors.add(bottom);
        }
        // left
        if(maze.isBelongToMaze(stateRow,stateColumn - 1) && !(mazeState.getPrecursor() != null &&((MazeState)mazeState.getPrecursor()).equal(stateRow,stateColumn - 1)))
        {
            left = new MazeState(new Position(stateRow,stateColumn - 1),10,mazeState);
            successors.add(left);
        }

        if(includeDiagonal)
        {
            // top - right
            if((top != null || right != null) && maze.isBelongToMaze(stateRow - 1,stateColumn + 1) && !(mazeState.getPrecursor() != null &&((MazeState)mazeState.getPrecursor()).equal(stateRow - 1,stateColumn + 1)))
            {
                successors.add(new MazeState(new Position(stateRow - 1,stateColumn + 1),15,mazeState));

            }
            // top - left
            if((top != null || left != null) && maze.isBelongToMaze(stateRow - 1,stateColumn - 1) && !(mazeState.getPrecursor() != null &&((MazeState)mazeState.getPrecursor()).equal(stateRow - 1,stateColumn - 1)))
            {
                successors.add(new MazeState(new Position(stateRow - 1,stateColumn - 1),15,mazeState));
            }
            // bottom - right
            if((bottom != null || right != null) && maze.isBelongToMaze(stateRow + 1,stateColumn + 1) && !(mazeState.getPrecursor() != null &&((MazeState)mazeState.getPrecursor()).equal(stateRow + 1,stateColumn + 1)))
            {
                successors.add(new MazeState(new Position(stateRow + 1,stateColumn + 1),15,mazeState));
            }
            // bottom - left
            if((bottom != null || left != null) && maze.isBelongToMaze(stateRow + 1,stateColumn - 1) && !(mazeState.getPrecursor() != null &&((MazeState)mazeState.getPrecursor()).equal(stateRow + 1,stateColumn - 1)))
            {
                successors.add(new MazeState(new Position(stateRow + 1,stateColumn - 1),15,mazeState));
            }
        }

        return successors;
    }
}
