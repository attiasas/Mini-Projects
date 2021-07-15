package Advance_Programming.Maze_Game_Part_A.algorithms.search;

import java.util.ArrayList;
import java.util.Collections;

/**
 * this class represents the solution state in a search problem
 * Created by Assaf Attias
 */
public class Solution extends AState
{
    /**
     * Constructor
     * @param cost      - the cost of the state
     * @param precursor - the state that is the precursor to this one (came from).
     */
    public Solution(double cost, AState precursor)
    {
        super(cost, precursor);
    }

    /**
     * Get an array list of states that represents the solution path to the search problem
     * @return - array list of states, from start to goal representing the solved path
     */
    public ArrayList<AState> getSolutionPath()
    {
        ArrayList<AState> path = new ArrayList<>();

        AState currentState = getPrecursor();

        while (currentState != null)
        {
            path.add(currentState);
            currentState = currentState.getPrecursor();
        }

        // sort from start to goal (instead of from goal to start)
        Collections.reverse(path);

        return path;
    }
}
