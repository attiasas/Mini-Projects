package AI_ML_DL.MDP_RL.src.sol;

import AI_ML_DL.MDP_RL.src.CoffeeWorldAction;
import AI_ML_DL.MDP_RL.src.CoffeeWorldState;
import AI_ML_DL.MDP_RL.src.OpenCoffeeEnvironment;

import java.util.List;
import java.util.Map;

public class BellmanUpdate {

    public static double getNewV(CoffeeWorldState state, OpenCoffeeEnvironment openEnvironment, double discountFactor, Map<CoffeeWorldState, Double> vValues) {

        // TODO: Compute the new V value for the given state.
        double maxV=Double.NEGATIVE_INFINITY;
        double v;
        double reward;

        for(CoffeeWorldAction action : openEnvironment.getLegalActions(state)){
            reward = openEnvironment.getReward(state,action);
            v=reward;
            for(OpenCoffeeEnvironment.Transition transition : openEnvironment.getLegalTransitions(state,action))
                v += discountFactor*transition.probability * vValues.get(transition.state);
            if(maxV<v)
            {
                maxV=v;
            }
        }



        // Helper values:
        //List<CoffeeWorldAction> legalActions = openEnvironment.getLegalActions(state);
        //CoffeeWorldAction action = legalActions.get(0); // An example of one action
        //List<OpenCoffeeEnvironment.Transition> transitions = openEnvironment.getLegalTransitions(state,action);

        return maxV;
    }

}
