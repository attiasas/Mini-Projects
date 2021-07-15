package Advance_Programming.Maze_Game_Part_A.algorithms.search;

/**
 * This interface represents an algorithm to solve a search problem
 * Created by Assaf Attias
 */
public interface ISearchingAlgorithm
{
    /**
     * solve a given search problem
     * @param problem - a searchable object implementing a search problem
     * @return - Solution state object of the given problem
     */
    Solution solve(ISearchable problem);

    /**
     * get the number of nodes evaluated during the solve process
     * @return - number of nodes evaluated during the solve process
     */
    int getNumberOfNodesEvaluated();

    /**
     * the name of the algorithm
     * @return - a string representing the name of the algorithm
     */
    String getName();
}
