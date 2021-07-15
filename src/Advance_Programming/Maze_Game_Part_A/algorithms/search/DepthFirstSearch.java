package Advance_Programming.Maze_Game_Part_A.algorithms.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/**
 * this class implements DepthFirst search algorithm
 * Created by Assaf Attias
 */
public class DepthFirstSearch extends ASearchingAlgorithm
{

    @Override
    public Solution solve(ISearchable problem)
    {
        // validate
        if(problem == null || problem.getGoalState() == null || problem.getStartState() == null) return null;

        // reset
        numOfVisitedNodes = 0;
        Stack<AState> openStack = new Stack<>();
        HashSet<AState> visited = new HashSet<>();

        AState start = problem.getStartState();
        openStack.push(start);

        AState goal = problem.getGoalState();

        while (!openStack.isEmpty())
        {
            // pop
            AState currentState = openStack.pop();
            numOfVisitedNodes++;
            visited.add(currentState);

            // get successors
            ArrayList<AState> successors = problem.getAllPossibleStates(currentState);
            for (AState successor : successors)
            {
                if(!visited.contains(successor))
                {
                    successor.setPrecursor(currentState);

                    if(successor.equals(goal))
                    {
                        return new Solution(successor.getCost(),successor);
                    }

                    openStack.push(successor);
                }
            }
        }

        return null; // no solution
    }

    @Override
    public String getName() {
        return "Depth First Search";
    }
}
