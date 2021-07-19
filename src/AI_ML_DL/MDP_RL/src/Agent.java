package AI_ML_DL.MDP_RL.src;

public abstract class Agent {
    protected CoffeeEnvironment environment;
    protected double discountFactor;

    public Agent(CoffeeEnvironment environment, double discountFactor){
        this.environment = environment;
        this.discountFactor = discountFactor;
    }

    abstract public CoffeeEnvironment.ActionOutcome act(CoffeeWorldState state);
}
