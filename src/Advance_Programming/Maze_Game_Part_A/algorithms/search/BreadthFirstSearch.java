package Advance_Programming.Maze_Game_Part_A.algorithms.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * this class implements BreadthFirst search algorithm
 * Created by 308214899 205381684
 */
public class BreadthFirstSearch extends ASearchingAlgorithm
{
    @Override
    public Solution solve(ISearchable problem)
    {
        // validate
        if(problem == null || problem.getGoalState() == null || problem.getStartState() == null) return null;

        // init
        reset();
        HashSet<AState> closeList = new HashSet<>();
        HashMap<Integer,AState> inOpenList = new HashMap<>();

        AState currentState = problem.getStartState();
        AState goal = problem.getGoalState();

        while (currentState != null && !currentState.equals(goal))
        {
            if(!closeList.contains(currentState))
            {
                ArrayList<AState> successors = problem.getAllPossibleStates(currentState);
                for(int i = 0; i < successors.size(); i++)
                {
                    AState successor = successors.get(i);
                    applyCost(successor,currentState.getCost());
                    successor.setPrecursor(currentState);

                    if(!inOpenList.containsKey(successor.hashCode())) // new State
                    {
                        openStateList.add(successor);
                        inOpenList.put(successor.hashCode(),successor);
                    }
                    else
                    {
                        if(inOpenList.get(successor.hashCode()).getCost() > successor.getCost()) // update old state
                        {
                            inOpenList.get(successor.hashCode()).setCost(successor.getCost());
                            inOpenList.get(successor.hashCode()).setPrecursor(currentState);
                            openStateList.offer(openStateList.poll()); // activate heapify
                        }

                    }
                }

                closeList.add(currentState);
            }

            currentState = getNextNode();
            inOpenList.remove(currentState);
        }

        if(currentState == null) return null; // no solution
        return new Solution(currentState.getCost(),currentState); // found solution
    }

    /**
     * Update cost of a state base on a given cost, the algorithm use that to indicate the current cost base on the precursor
     * @param state - state to update cost on
     * @param addCost - extra cost to add
     */
    protected void applyCost(AState state, double addCost)
    {
        state.setCost(addCost + 1);
    }

    @Override
    public String getName() {
        return "Breadth First Search";
    }
}
