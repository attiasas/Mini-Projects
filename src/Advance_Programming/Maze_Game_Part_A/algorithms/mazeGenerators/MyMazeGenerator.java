package Advance_Programming.Maze_Game_Part_A.algorithms.mazeGenerators;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a maze generator for advanced random mazes
 * Created by Assaf Attias
 */
public class MyMazeGenerator extends AMazeGenerator
{
    // help variables
    private ArrayList<Position> unvisitedTiles;
    private int[][] maze;
    private boolean[][] visited;

    @Override
    public Maze generate(int rows,int columns)
    {
        // validate
        if(rows < 1) rows = 2;
        if(columns < 1)columns = 2;

        Position start = new Position((int)(Math.random() * rows),(int)(Math.random() * columns));

        // init walls
        maze = new int[rows][columns];
        for(int row = 0; row < maze.length; row++) for(int col = 0; col < maze[row].length; col++) if(!start.equals(row,col)) maze[row][col] = 1;

        // apply a version of prim algorithm to generate maze
        // init
        visited = new boolean[rows][columns];
        visited[start.getRowIndex()][start.getColumnIndex()] = true;
        unvisitedTiles = new ArrayList<>();

        unvisitedTiles.addAll(getNeighbors(start.getRowIndex(),start.getColumnIndex()));
        while (!unvisitedTiles.isEmpty())
        {
            // pick random tile from list
            Position chosenTile = unvisitedTiles.remove((int)(Math.random() * unvisitedTiles.size()));
            int chosenRowIndex = chosenTile.getRowIndex();
            int chosenColIndex = chosenTile.getColumnIndex();

            // decide if tile is part of the maze
            if(hasOneNeighborInMaze(chosenRowIndex,chosenColIndex))
            {
                visited[chosenRowIndex][chosenColIndex] = true;
                maze[chosenRowIndex][chosenColIndex] = 0;

                unvisitedTiles.addAll(getNeighbors(chosenRowIndex,chosenColIndex));
            }
        }

        // get random goal
        Position goal = getRandomGoal(rows, columns, start);

        return new Maze(start,goal,maze);
    }

    /**
     * Generate random goal from a cell of the maze
     * @return - the position of goal
     */
    private Position getRandomGoal(int rows, int columns, Position start)
    {
        Position goal = null;

        while (goal == null)
        {
            int row = (int)(Math.random() * rows);
            int col = (int)(Math.random() * columns);
            if (maze[row][col] == 0 && !start.equals(row,col)) goal = new Position(row,col);
        }

        return goal;
    }

    /**
     * check if a given cell has exactly one unvisited neighbor
     * @param row - rowIndex of cell
     * @param col - columnIndex of cell
     * @return true if only one unvisited
     */
    private boolean hasOneNeighborInMaze(int row, int col)
    {
        if(visited[row][col]) return false;
        int count = 0;

        if((row - 1) >= 0 && maze[row - 1][col] == 0) count++;
        if((row + 1) < maze.length && maze[row + 1][col] == 0) count++;
        if((col - 1) >= 0 && maze[row][col - 1] == 0) count++;
        if((col + 1) < maze[row].length && maze[row][col + 1] == 0) count++;

        return count == 1;
    }
    /**
     * Get list of unvisited neighbors positions that are not in the wall list
     * @param row - row index of position to check
     * @param col - column index of position to check
     * @return - list of positions
     */
    private List<Position> getNeighbors(int row, int col)
    {
        ArrayList<Position> list = new ArrayList<>();

        // left
        if((row - 1) >= 0 && !visited[row - 1][col])
        {
            list.add(new Position(row - 1,col));
        }
        // right
        if((row + 1) < maze.length && !visited[row + 1][col])
        {
            list.add(new Position(row + 1,col));
        }
        // top
        if((col - 1) >= 0 && !visited[row][col - 1])
        {
            list.add(new Position(row,col - 1));
        }
        // bottom
        if((col + 1) < maze[row].length && !visited[row][col + 1])
        {
            list.add(new Position(row,col + 1));
        }

        return list;
    }
}
