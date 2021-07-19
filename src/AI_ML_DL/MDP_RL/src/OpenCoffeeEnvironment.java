package AI_ML_DL.MDP_RL.src;

import java.util.ArrayList;
import java.util.List;

/**
 * Another Coffee World environment. This environment is equivalent ot the CoffeeEnvironment,
 * except that it exposes also the transition funciton and the reward function.
 */
public class OpenCoffeeEnvironment extends CoffeeEnvironment {
    public double[][] spillProbabilities;
    public OpenCoffeeEnvironment(int rows, int cols, double[][] spillProbability) {
        super(rows, cols, spillProbability);
        this.spillProbabilities = spillProbability;

    }

    /**
     * Returns a list with all the states in the world.
     */
    public List<CoffeeWorldState> getStates(){
        List<CoffeeWorldState> allStates = new ArrayList<>(this.rows*this.cols + 1);
        for(int i=0;i<this.rows;i++)
            for(int j=0;j<this.cols;j++)
                allStates.add(this.states[i][j]);
        allStates.add(this.goalState);
        return allStates;
    }

    /**
     * For every state s and action a,
     * this method returns a list of states that can be reached by applying a to s,
     * and the probability of reaching that state.
     */
    public List<Transition> getLegalTransitions(CoffeeWorldState state, CoffeeWorldAction action){
        List<Transition> transitions;
        if(CoffeeWorldAction.DRINK_COFFEE.equals(action)){
            transitions = new ArrayList<Transition>(1);
            transitions.add(new Transition(this.goalState,1.0));
        }
        else{
            // Else, this is regular move action.
            transitions = new ArrayList<Transition>(2);
            int newRow = state.robotAtRow + action.deltaRows;
            int newCol = state.robotAtCol + action.deltaCols;
            assert(this.isLegal(state,action));
            transitions.add(new Transition(this.states[newRow][newCol],
                    1-this.spillProbabilities[state.robotAtRow][state.robotAtCol]));
            transitions.add(new Transition(this.goalState,
                    this.spillProbabilities[state.robotAtRow][state.robotAtCol]));
        }

        return transitions;
    }

    public class Transition{
        public CoffeeWorldState state;
        public double probability;
        public Transition(CoffeeWorldState state, double probability){
            this.state = state;
            this.probability = probability;
        }
    }
}
