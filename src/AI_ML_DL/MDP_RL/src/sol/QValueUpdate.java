package AI_ML_DL.MDP_RL.src.sol;

import AI_ML_DL.MDP_RL.src.AbstractQLearningAgent;
import AI_ML_DL.MDP_RL.src.CoffeeEnvironment;
import AI_ML_DL.MDP_RL.src.CoffeeWorldAction;
import AI_ML_DL.MDP_RL.src.CoffeeWorldState;

import java.util.List;
import java.util.Random;

public class QValueUpdate {

    public static double newQValue(double oldQValue, double reward, double nextStatesMaxQ, double discountFactor, double learningRate) {
        // TODO: Replace this with the Q-learning update rule
        return (1-learningRate) * oldQValue + (learningRate) * (reward + discountFactor * nextStatesMaxQ);

    }

    public static CoffeeWorldAction chooseAction(CoffeeWorldState state, double epsilon, CoffeeEnvironment environment, AbstractQLearningAgent agent)
    {
        double random = Math.random();
        double maxQ = 0;
        int maxIndex = 0;
        List<CoffeeWorldAction> actions = environment.getLegalActions(state);

        for(int i = 0; i < actions.size(); i++)
        {
            CoffeeWorldAction action = actions.get(i);
            double nextActionQ = agent.getQValue(state,action);
            if(maxQ < nextActionQ)
            {
                maxQ = nextActionQ;
                maxIndex = i;
            }
        }

        if(random < epsilon)
        {
            // TODO: Explore: choose a random action with epsilon probability
            return actions.get((int)(Math.random() * actions.size()));
        }
        else
        {
            // TODO: Exploit: get the best (highest Q value) action legal in this state
            return actions.get(maxIndex);
        }
    }

}
