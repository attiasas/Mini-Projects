package Advance_Programming.Maze_Game_Part_A.algorithms.mazeGenerators;

/**
 * This class is a maze generator for empty mazes
 * Created by Assaf Attias
 */
public class EmptyMazeGenerator extends AMazeGenerator
{
    @Override
    public Maze generate(int rows,int columns)
    {
        Position start = new Position((int)(Math.random() * rows),(int)(Math.random() * columns));
        Position goal = new Position((int)(Math.random() * rows),(int)(Math.random() * columns));

        while (goal.equals(start)) goal = new Position((int)(Math.random() * rows),(int)(Math.random() * columns));

        return new Maze(start,goal,new int[rows][columns]);
    }
}
