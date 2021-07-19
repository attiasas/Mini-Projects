package AI_ML_DL.MDP_RL.src;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class QValues {

    private Map<CoffeeWorldState,Map<CoffeeWorldAction,Double>> qValues;

    public QValues(){
        this.qValues = new HashMap<CoffeeWorldState,Map<CoffeeWorldAction,Double>>();
    }

    /**
     * Get the current Q value of the given state and action pair
     */
    public double getQValue(CoffeeWorldState state, CoffeeWorldAction action){
        Map<CoffeeWorldAction,Double> actionToQ = this.getActionToQValue(state);
        if(actionToQ.containsKey(action))
            return actionToQ.get(action);
        else{
            return 0.0; // First time this action is performed in this state, Q value is zero.
        }
    }

    /**
     * Get the current Q value of the given state and action pair
     */
    public void setQValue(CoffeeWorldState state, CoffeeWorldAction action, double newValue){
        Map<CoffeeWorldAction,Double> actionToQ = this.getActionToQValue(state);
        actionToQ.put(action, newValue);
    }

    /**
     * Get the q value stored for every action applicable in the given state.
     */
    private Map<CoffeeWorldAction, Double> getActionToQValue(CoffeeWorldState state){
        if(this.qValues.containsKey(state)==false){
            this.qValues.put(state,new HashMap<CoffeeWorldAction,Double>());
        }
        return this.qValues.get(state);
    }
}
