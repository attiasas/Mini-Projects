package Advance_Programming.Maze_Game_Part_A.algorithms.mazeGenerators;

/**
 * This class represents a abstract base class for maze generators
 * Created by Assaf Attias
 */
public abstract class AMazeGenerator implements IMazeGenerator
{
    @Override
    public long measureAlgorithmTimeMillis(int rows,int columns)
    {
        long startTime = System.currentTimeMillis();
        generate(columns,rows);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}
