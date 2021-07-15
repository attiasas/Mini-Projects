package Advance_Programming.Maze_Game_Part_A.algorithms.search;

/**
 * This class represents an abstract state of a search problem
 * Created by Assaf Attias
 */
public abstract class AState implements Comparable
{
    private double cost;
    private AState precursor;

    /**
     * Constructor
     * @param cost - the cost of the state
     * @param precursor - the state that is the precursor to this one (came from).
     */
    public AState(double cost, AState precursor)
    {
        this.cost = cost;
        this.precursor = precursor;
    }

    /**
     * Constructor
     */
    public AState()
    {
        this.cost = 0;
        this.precursor = null;
    }

    /**
     * Set the cost of the current state
     * @param cost - double
     */
    public void setCost(double cost) { this.cost = cost; }

    /**
     * Getter for the cost of the state
     * this method is used in the compare method. (override in need to change the priority of a state)
     * @return - cost of the state
     */
    public double getCost() { return cost; }

    /**
     * Getter for the precursor of the state
     * @return - precursor of the state
     */
    public AState getPrecursor() { return precursor; }

    /**
     *  set the precursor of the state
     * @param precursor
     */
    public void setPrecursor(AState precursor)
    {
        this.precursor = precursor;
    }

    @Override
    /**
     * compare using the method --> getCost()
     */
    public int compareTo(Object o)
    {
        if(!(o instanceof AState)) return -1;

        return (int)(getCost() - ((AState)o).getCost());
    }
}
