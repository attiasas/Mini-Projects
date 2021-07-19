package AI_ML_DL.MDP_RL.src;

import java.util.Random;

/**
 * This class enables running the RL agent and measuring its performance.
 */
public class Runner {

    /**
     * Runs a single episode. The agent chooses an action, learns from its outcome,
     * and continues doing so until reaching a terminal state.
     * @return The discounted rewards collected by the agent.
     */
    public double runEpisode(CoffeeEnvironment environment, Agent agent, double discountFactor) {
        CoffeeWorldState currentState = environment.getInitialState();
        CoffeeEnvironment.ActionOutcome outcome;
        double accumulatedReward = 0;
        double discount = 1.0;

        while (environment.isTerminal(currentState) == false) {
            outcome = agent.act(currentState);
            accumulatedReward = accumulatedReward + discount * outcome.reward;
            discount = discount * discountFactor; // Future rewards get discounted
            currentState = outcome.newState;
        }
        return accumulatedReward;
    }

    /**
     * Run multiple episodes, return the average discounted collected rewards.
     */
    public double runEpisodes(CoffeeEnvironment environment, Agent agent, double discountFactor, int iterations) {
        double sumOfRewards = 0.0;
        for (int i = 0; i < iterations; i++)
            sumOfRewards += this.runEpisode(environment,agent, discountFactor);
        return sumOfRewards / iterations;
    }

    /**
     * Create a random matrix that defines the probability to spill each coffee in each grid cell.
     */
    private double[][] createRandomSpillProbabilities(int rows, int cols) {
        Random random = new Random();
        double[][] spillProbabilities = new double[rows][cols];
        for (int i=0;i<rows;i++)
            for(int j=0;j<cols;j++)
                spillProbabilities[i][j] = random.nextDouble();
        return spillProbabilities;
    }

    /**
     * For debug purposes, here's a very simple instance of the Coffee World problem.
     */
    private double[][] createSimpleSpillProbabilities(int rows, int cols) {
        double[][] spillProbabilities = new double[rows][cols];
        spillProbabilities[0][0]=0.0;
        spillProbabilities[0][1]=0.0;
        spillProbabilities[0][2]=0.0;
        spillProbabilities[1][0]=0.9;
        spillProbabilities[1][1]=0.9;
        spillProbabilities[1][2]=0.0;
        spillProbabilities[2][0]=0.9;
        spillProbabilities[2][1]=0.9;
        spillProbabilities[2][2]=0.0;
        return spillProbabilities;
    }

    /**
     * Print out to the standard output the spill probability of each cell.
     * Useful for debugging.
     */
    public static void print(double[][] spillProbabilities){
        StringBuilder builder;
        for(int i=0;i<spillProbabilities.length;i++){
            // Print header
            if(i==0){
                builder = new StringBuilder();
                builder.append(" \t ");
                for(int j=0;j<spillProbabilities[0].length;j++)
                    builder.append(String.format("   %d\t", j));
                System.out.println(builder.toString());
            }

            // Print content
            builder = new StringBuilder();
            builder.append(i +"\t ");
            for(int j=0;j<spillProbabilities[i].length;j++){
                builder.append(String.format("%.2f\t", spillProbabilities[i][j]));
            }
            System.out.println(builder.toString());
        }
    }

    /**
     * The main method. Runs the RL and the MDP agents
     */
    public static void main(String[] args){
        Runner runner = new Runner();
        int rows = 3;
        int cols = 3;
        double discountFactor = 0.75;
        int iterations = 10000;

        double[][] randomSpillProbabilities  = runner.createRandomSpillProbabilities(rows,cols);
        System.out.println("Run with random probabilities:");
        run(runner, randomSpillProbabilities, rows, cols, discountFactor, iterations);

        double[][] exampleSpillProbabilities  = runner.createSimpleSpillProbabilities(rows,cols);
        System.out.println("\nRun with pre-generated probabilities:");
        run(runner, exampleSpillProbabilities, rows, cols, discountFactor, iterations);
    }

    private static void run(Runner runner, double[][] spillProbabilities, int rows, int cols, double discountFactor,
                            int iterations){
        double utility = 0;

        // Print spill probabilities for debug purposes
        runner.print(spillProbabilities);

        // MDP agent
        OpenCoffeeEnvironment openEnvironment = new OpenCoffeeEnvironment(rows,cols, spillProbabilities);
        MDPAgent VIAgent;
        VIAgent= new ValueIterationAgent(openEnvironment, discountFactor);
        VIAgent.plan();
        utility = runner.runEpisodes(openEnvironment, VIAgent, discountFactor, iterations);
        System.out.format("MDP: Average utility over %d iterations is %.2f%n", iterations,utility);

        // Reinforcement learning agent
        CoffeeEnvironment environment = new CoffeeEnvironment(rows,cols, spillProbabilities);
        ReinforcementLearningAgent agent;
        double epsilon = 0.1;
        agent = new EpsilonGreedyAgent(environment, discountFactor,epsilon);
        for(int i=100; i<=iterations;i=i*2) {
            utility = runner.runEpisodes(environment, agent, discountFactor, i);
            System.out.format("RL: Average utility over %d iterations is %.2f%n", i, utility);
        }
    }

}
