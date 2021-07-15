package Advance_Programming.Maze_Game_Part_A.algorithms.mazeGenerators;

import algorithms.search.MazeState;
import java.util.ArrayList;

/**
 * This class represents a maze
 * Created by Assaf Attias
 */
public class Maze
{
    private Position startPosition;
    private Position goalPosition;
    private int[][] maze;

    /**
     * Parameters Constructor
     * @param startPosition - position object representing the start position coordinates
     * @param goalPosition - position object representing the goal position coordinates
     * @param maze - int[][] representing the maze, 0 = path | 1 = wall
     */
    public Maze(Position startPosition, Position goalPosition, int[][] maze)
    {
        this.startPosition = new Position(startPosition);
        this.goalPosition = new Position(goalPosition);
        setMaze(maze);
    }

    /**
     * Get the maze representation in int[][]
     * @return - int[][] maze
     */
    public int[][] getMaze() { return maze; }

    /**
     * Set the maze base on an int array template
     * @param mazeTemplate - int[][] not null, with only values of 1 or 0.
     * @return result of the operation
     */
    public boolean setMaze(int[][] mazeTemplate)
    {
        if(mazeTemplate == null || mazeTemplate.length <= 0) return false;

        int[][] temp = new int[mazeTemplate.length][];

        for(int row = 0; row < temp.length; row++)
        {
            if(mazeTemplate[row].length <= 0) return false;
            temp[row] = new int[mazeTemplate[row].length];

            for(int column = 0; column < temp[row].length; column++)
            {
                if(mazeTemplate[row][column] != 0 && mazeTemplate[row][column] != 1) return false;
                temp[row][column] = mazeTemplate[row][column];
            }
        }

        maze = temp;
        return true;
    }

    /**
     * returns the start posiion of the maze
     * @return - maze position object
     */
    public Position getStartPosition() { return new Position(startPosition); }

    /**
     * returns the goal posiion of the maze
     * @return - maze psosition object
     */
    public Position getGoalPosition() { return new Position(goalPosition); }

    /**
     * check if a given cell (row & column indexes) is part of the maze or wall
     * @param row - rowIndex of the maze
     * @param col - columnIndex of the maze
     * @return - true if part of the maze, false otherwise
     */
    public boolean isBelongToMaze(int row, int col)
    {
        if(maze == null || row < 0 || col < 0 || row >= maze.length || col >= maze[row].length) return false;

        return maze[row][col] == 0;
    }

    /**
     * print the maze (FORMAT: 1 = wall, 0 = empty, S = start, E = goal)
     */
    public void print()
    {
        String result = "";
        for(int row = 0; row < maze.length; row++)
        {
            result += "{";

            for (int column = 0; column < maze[row].length; column++)
            {
                if(startPosition.equals(row,column)) result += "S";
                else if(goalPosition.equals(row,column)) result += "E";
                else result += "" + maze[row][column];

                if(column < maze[row].length - 1) result += ",";
            }

            result += "}";
            if(row < maze.length - 1) result += "\n";
        }

        System.out.println(result);
    }

}
