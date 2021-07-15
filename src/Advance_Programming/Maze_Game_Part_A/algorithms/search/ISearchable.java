package Advance_Programming.Maze_Game_Part_A.algorithms.search;

import java.util.ArrayList;

/**
 * This Interface defines a searchable object for a search problem
 * Created by Assaf Attias
 */
public interface ISearchable
{
    /**
     * Get the initial state of the problem
     * @return - AState object that represents the start
     */
    AState getStartState();

    /**
     * Get the Goal state of the problem
     * @return - AState object that represents the goal
     */
    AState getGoalState();

    /**
     * Get all the successors of a given state
     * @param state - a state to get the successors from
     * @return - array list of all the successors states of the given state
     */
    ArrayList<AState> getAllPossibleStates(AState state);
}
