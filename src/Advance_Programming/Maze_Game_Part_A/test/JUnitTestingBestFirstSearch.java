package Advance_Programming.Maze_Game_Part_A.test;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.BestFirstSearch;
import algorithms.search.MazeState;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JUnitTestingBestFirstSearch
{
    @Test
    void solve()
    {
        BestFirstSearch searcher = new BestFirstSearch();
        int[][] mazeBoard = {{0,0,0,0},{1,1,0,0},{0,0,0,0}};
        Position start = new Position(2,3);
        Position goal = new Position(0,0);
        Maze maze = new Maze(start,goal,mazeBoard);
        SearchableMaze searchableMaze = new SearchableMaze(maze);

        Solution solution = searcher.solve(searchableMaze);

        assertEquals(9,searcher.getNumberOfNodesEvaluated());
        assertEquals(4,solution.getSolutionPath().size());
        assertEquals(40,solution.getCost());
        assertTrue(solution.getSolutionPath().contains(new MazeState(new Position(1,2))));
    }

    @Test
    void solveWrongMaze()
    {
        MyMazeGenerator generator = new MyMazeGenerator();
        Maze maze = generator.generate(0,0);
        SearchableMaze searchableMaze = new SearchableMaze(maze);
        BestFirstSearch searcher = new BestFirstSearch();

        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                searcher.solve(searchableMaze);
            }
        };
        assertDoesNotThrow(executable);
    }

    @Test
    void solveNull()
    {
        BestFirstSearch searcher = new BestFirstSearch();
        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                searcher.solve(null);
            }
        };
        assertDoesNotThrow(executable);
    }

    @Test
    void getName()
    {
        BestFirstSearch search = new BestFirstSearch();
        assertEquals("Best First Search", search.getName());
    }

}