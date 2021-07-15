package Advance_Programming.Maze_Game_Part_B.src.test;

import algorithms.mazeGenerators.*;

public class RunMazeGenerator
{
    public static void main(String[] args)
    {
        //testMazeGenerator(new EmptyMazeGenerator());
        //testMazeGenerator(new SimpleMazeGenerator());
        for (int i = 0; i < 10; i++)testMazeGenerator(new MyMazeGenerator());
    }

    private static void testMazeGenerator(IMazeGenerator mazeGenerator)
    {
        // prints the time it takes the algorithm to run
        System.out.println(String.format("Maze generation time(ms): %s",mazeGenerator.measureAlgorithmTimeMillis(30/*rows*/,30/*columns*/)));
        // generate another maze
        Maze maze = mazeGenerator.generate(30/*rows*/,30/*columns*/);
        // prints the maze
        maze.print();
        // get the position
        Position startPosition = maze.getStartPosition();
        // print the position
        System.out.println(String.format("Start Position: %s",startPosition));
        // prints the maze exit position
        System.out.println(String.format("Goal Position: %s",maze.getGoalPosition()));
    }
}
