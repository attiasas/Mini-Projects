package AI_ML_DL.MDP_RL.src;

public abstract class ReinforcementLearningAgent extends Agent{
    public ReinforcementLearningAgent(CoffeeEnvironment environment, double discountFactor){
        super(environment, discountFactor);
    }

    /**
     * Execute an action, and learn from its outcome.
     * @return the outcome of the action -- the new state and the obtained reward.
     */
    public CoffeeEnvironment.ActionOutcome act(CoffeeWorldState state){
        CoffeeWorldAction action = this.chooseAction(state);
        CoffeeEnvironment.ActionOutcome outcome = this.environment.apply(state,action);
        this.learn(state,action, outcome.newState,outcome.reward);

        return outcome;
    }


    public abstract CoffeeWorldAction chooseAction(CoffeeWorldState state);

    public abstract void learn(CoffeeWorldState state,
                      CoffeeWorldAction action,
                      CoffeeWorldState newState,
                      double reward);
}
