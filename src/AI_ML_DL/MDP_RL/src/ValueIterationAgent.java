package AI_ML_DL.MDP_RL.src;

import AI_ML_DL.MDP_RL.src.sol.BellmanUpdate;

import java.util.HashMap;
import java.util.Map;

/**
 * An MDP solving agent that runs the value iteration algorithm.
 */
public class ValueIterationAgent extends MDPAgent {
    protected Map<CoffeeWorldState, Double> vValues; // Maps a state to its V value

    public ValueIterationAgent(OpenCoffeeEnvironment environment, double discountFactor){
        super(environment, discountFactor);
    }

    /**
     * Choose the best action to perform in the given state, according to the stored V values;
     */
    @Override
    public CoffeeWorldAction chooseAction(CoffeeWorldState state) {
        // No choosing of actions in a terminal state
        if (this.environment.isTerminal(state))
            return null;

        OpenCoffeeEnvironment openEnvironment = (OpenCoffeeEnvironment) this.environment;
        double maxV=Double.NEGATIVE_INFINITY;
        double v;
        double reward;
        CoffeeWorldAction bestAction=null;
        for(CoffeeWorldAction action : environment.getLegalActions(state)){
            reward = environment.getReward(state,action);
            v=reward;
            for(OpenCoffeeEnvironment.Transition transition : openEnvironment.getLegalTransitions(state,action))
                v += this.discountFactor*transition.probability* this.vValues.get(transition.state);
            if(maxV<v){
                maxV=v;
                bestAction = action;
            }
        }
        return bestAction;
    }

    /**
     * Run the value iteration algorithm until it converges to an error smaller than 0.001.
     */
    @Override
    public void plan() {
        OpenCoffeeEnvironment openEnvironment = (OpenCoffeeEnvironment) this.environment;
        this.vValues = new HashMap<CoffeeWorldState,Double>();

        // Initialize V values to zero
        for(CoffeeWorldState state : openEnvironment.getStates()){
            this.vValues.put(state,0.0);
        }

        int iteration = 0;
        double newV;
        double oldV;
        double vDiff;
        double maxVDiff;
        do{
            maxVDiff=0;
            for(CoffeeWorldState state : openEnvironment.getStates()){
                if(this.environment.isTerminal(state)==false) {
                    oldV = this.vValues.get(state);
                    bellmanUpdate(state);
                    newV = this.vValues.get(state);

                    // Check diff to estimate error
                    vDiff = Math.abs(newV - oldV);
                    if (vDiff > maxVDiff)
                        maxVDiff = vDiff;

                    // Update the V value
                    this.vValues.put(state, newV);
                }
            }
        }while(maxVDiff>0.001);
    }

    /**
     * Perform the Bellman update rule.
     * That is, update the V value of the given state.
     */
    public void bellmanUpdate(CoffeeWorldState state){
        OpenCoffeeEnvironment openEnvironment = (OpenCoffeeEnvironment) this.environment;

        double newV = BellmanUpdate.getNewV(state, openEnvironment, this.discountFactor, this.vValues);

        this.vValues.put(state,newV);
    }
}
