package AI_ML_DL.MDP_RL.src;

abstract public class MDPAgent extends Agent{
    public MDPAgent(OpenCoffeeEnvironment environment, double discountFactor){
        super(environment, discountFactor);
    }

    /**
     * Execute an action.
     * @return the outcome of the action -- the new state and the obtained reward.
     */
    public CoffeeEnvironment.ActionOutcome act(CoffeeWorldState state){
        CoffeeWorldAction action = this.chooseAction(state);
        return this.environment.apply(state,action);
    }

    abstract public CoffeeWorldAction chooseAction(CoffeeWorldState state);
    abstract public void plan();
}
