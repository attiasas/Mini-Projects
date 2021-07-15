package Advance_Programming.Maze_Game_Part_A.algorithms.search;

import algorithms.mazeGenerators.Position;

/**
 * this class represents a state in a maze problem
 * Created by Assaf Attias
 */
public class MazeState extends AState
{
    private Position state;

    /**
     * Constructor
     * @param state - position in maze that represents the state
     * @param cost      - the cost of the state
     * @param precursor - the state that is the precursor to this one (came from).
     */
    public MazeState(Position state, double cost, AState precursor)
    {
        super(cost, precursor);
        this.state = state;
    }

    /**
     * Constructor
     * @param state - position in maze that represents the state
     */
    public MazeState(Position state)
    {
        this.state = state;
    }


    /**
     * Constructor
     * @param state - position in maze that represents the state
     * @param precursor - the state that is the precursor to this one (came from).
     */
    public MazeState(Position state, AState precursor)
    {
        super(0, precursor);
        this.state = state;
    }

    /**
     * Get the current Position state stored in the state
     * @return - Position object of the state
     */
    public Position getState() { return state; }

    /**
     * Check if the state position is equal base on the coordinates it stores
     * @param row - rowIndex of a position in a maze
     * @param column - columnIndex of a position in a maze
     * @return
     */
    public boolean equal(int row, int column) { return state.equals(row, column); }

    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof MazeState)) return false;
        return state.equals(((MazeState)o).state);
    }

    @Override
    public int hashCode()
    {
        return state.hashCode();
    }

    @Override
    public String toString()
    {
        return state.toString();
    }
}
