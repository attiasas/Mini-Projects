package Advance_Programming.Maze_Game_Part_A.algorithms.search;

import java.util.PriorityQueue;

/**
 * this class represents an abstract search algorithm
 * Created by Assaf Attias
 */
public abstract class ASearchingAlgorithm implements ISearchingAlgorithm
{
    protected PriorityQueue<AState> openStateList;
    protected int numOfVisitedNodes;

    /**
     * Constructor
     * this class provides a openlist queue ordered by natural ordering defined by the State
     */
    public ASearchingAlgorithm()
    {
        reset();
    }

    /**
     * reset algorithm utils to initial conditions to use the algorithm again
     */
    protected void reset()
    {
        openStateList = new PriorityQueue<>();
        numOfVisitedNodes = 0;
    }

    /**
     * get the next state in the open list queue
     * @return - next node in open list queue
     */
    protected AState getNextNode()
    {
        if(openStateList.size() <= 0) return null;

        numOfVisitedNodes++;
        return openStateList.poll();
    }

    @Override
    public abstract Solution solve(ISearchable problem);

    @Override
    public abstract String getName();

    @Override
    public int getNumberOfNodesEvaluated() { return numOfVisitedNodes; }


}
