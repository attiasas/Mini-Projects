package Advance_Programming.Maze_Game_Part_A.algorithms.mazeGenerators;

/**
 * This interface defines a maze generator
 * Created by Assaf Attias
 */
public interface IMazeGenerator
{
    /**
     * Generate a Maze base on a given parameters
     * @param columns - number of columns in the maze
     * @param rows - number of columns in the maze
     * @return - Maze object
     */
    Maze generate(int rows, int columns);

    /**
     * Measure the time to generate a maze base on given parameters
     * @param columns - number of columns in the maze
     * @param rows - number of columns in the maze
     * @return - time elapsed to generate a maze
     */
    long measureAlgorithmTimeMillis(int rows, int columns);
}
