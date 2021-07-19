package AI_ML_DL.MDP_RL.src;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents the world's behavior in the Coffee World domain.
 * In this world, there is  rows x cols grid, where rows and cols are parameters.
 * The agent starts in grid cell (0,0) with a hot cup of coffee.
 * You are sitting in grid cell (rows-1, cols-1) and you want to drink your coffee.
 * The agent can move with the coffee cup to the four cardinal directions: up, down, left, and right,
 * as long as it stays on the grid. However, the floor is slippery,
 * and the agent might spill the coffee when it moves.
 * An episode ends either when the agent spilled the coffee,
 * or when the agent brings you the coffee and you drink it.
 *
 * Details:
 * 1. The reward for drinking the coffee is 100.
 * 2. You probability of spilling the coffee can be different for different grid cells.
 * Note that these probabilities are not known to the agent (they are private).
 */
public class CoffeeEnvironment {

    private Random randomGenerator;

    public int rows; // Number of rows in the grid
    public int cols; // Numver of columns in the grid
    private double spillProbability[][]; // The probability to spill the coffee in each grid cell

    // All the non-goal states
    protected CoffeeWorldState states[][];

    // The special states
    private CoffeeWorldState initialState; // This is where the coffee machine is
    private CoffeeWorldState coffeeAtHandState; // This is where the coffee is in my hand

    protected CoffeeWorldState goalState; // This is a dummy state representing the happiness after drinking the coffee

    public CoffeeEnvironment(int rows, int cols, double[][] spillProbability){
        this.randomGenerator = new Random();
        this.rows = rows;
        this.cols = cols;
        this.spillProbability = spillProbability;

        // Create the states
        this.states = new CoffeeWorldState[this.rows][this.cols];
        for(int i =0; i<this.rows;i++)
            for (int j = 0; j < this.cols; j++)
                this.states[i][j] = new CoffeeWorldState(i,j);

        // Set special states
        this.initialState = this.states[0][0];
        this.coffeeAtHandState = this.states[rows-1][cols-1];
        this.goalState = new CoffeeWorldState(rows, cols);
    }

    /**
     * Return a world state representing that the robot is at location (i,j) with coffee.
     */
    public CoffeeWorldState getState(int row,int col){
        return this.states[row][col];
    }

    /**
     * Check if an action can be performed at a given state.
     */
    public boolean isLegal(CoffeeWorldState state, CoffeeWorldAction action){
        // If the coffee is in my hand, I will drink it
        if (this.coffeeAtHandState.equals(state))
            return CoffeeWorldAction.DRINK_COFFEE.equals(action);

        // Else, the robot needs to move, so every move action that is in the grid is Ok
        int newRow = state.robotAtRow + action.deltaRows;
        int newCol = state.robotAtCol + action.deltaCols;
        if ((newRow<0)||(newRow>=rows))
            return false;
        if ((newCol<0)||(newCol>=cols))
            return false;
        return true;
    }

    /**
     * The last state is after drinking the coffee
     */
    public boolean isTerminal(CoffeeWorldState state){
        if (state.equals(this.goalState))
            return true;
        else return false;
    }

    /**
     * Drinking coffee gives reward. All other actions - no.
     */
    public double getReward(CoffeeWorldState state, CoffeeWorldAction action){
        if (this.coffeeAtHandState.equals(state)
                && (CoffeeWorldAction.DRINK_COFFEE.equals(action)))
            return 100.0;
        else return 0.0;
    }
    /**
     * Get all the legal actions for a given state.
     */
    public List<CoffeeWorldAction> getLegalActions(CoffeeWorldState state){
        List<CoffeeWorldAction> legalActions = new ArrayList<>(4);

        if(this.coffeeAtHandState.equals(state)){
            legalActions.add(CoffeeWorldAction.DRINK_COFFEE);
        }
        else {
            for (CoffeeWorldAction action : CoffeeWorldAction.MOVE_ACTIONS) {
                if (this.isLegal(state, action))
                    legalActions.add(action);
            }
        }
        return legalActions;
    }


    /**
     * Apply an action to a state. Spill the coffee and return the the start with probability
     * according to this.spillFactor.
     */
    public ActionOutcome apply(CoffeeWorldState state, CoffeeWorldAction action){
        // Check if this is the last action -- drink coffee
        double reward = this.getReward(state,action);
        if (CoffeeWorldAction.DRINK_COFFEE.equals(action)) {
            assert (this.coffeeAtHandState.equals(state));
            return new ActionOutcome(this.goalState,reward);
        }

        // Else, this is regular move action.
        int newRow = state.robotAtRow + action.deltaRows;
        int newCol = state.robotAtCol + action.deltaCols;

        assert(this.isLegal(state,action));
        if(this.randomGenerator.nextFloat()>this.spillProbability[newRow][newCol]){
            return new ActionOutcome(this.states[newRow][newCol],reward);
        }
        else{ // Spilled the coffee
            return new ActionOutcome(this.goalState,reward);
        }
    }

    // A getter for the initial state
    public CoffeeWorldState getInitialState() {
        return this.initialState;
    }

    /**
     * Internal class, representing action outcomes.
     */
    public class ActionOutcome {
        public CoffeeWorldState newState;
        public double reward;

        private ActionOutcome(CoffeeWorldState newState, double reward){
            this.newState=newState;
            this.reward=reward;
        }
    }

    public void print(){
        // Print the environment. For debug purposes
        StringBuilder stringBuilder;
        DecimalFormat stringFormat = new DecimalFormat("0.00");
        for(int i=0; i<this.rows;i++) {
            stringBuilder = new StringBuilder();
            for (int j = 0; j < this.cols;j++) {
                stringBuilder.append(stringFormat.format(this.spillProbability[i][j]));
                stringBuilder.append("\t");
            }
            System.out.println(stringBuilder.toString());
        }
    }
}
