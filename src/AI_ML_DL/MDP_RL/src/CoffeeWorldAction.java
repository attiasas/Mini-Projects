package AI_ML_DL.MDP_RL.src;

public class CoffeeWorldAction {
    public static CoffeeWorldAction UP = new CoffeeWorldAction("Down", +1,0);
    public static CoffeeWorldAction DOWN = new CoffeeWorldAction("Up", -1,0);
    public static CoffeeWorldAction RIGHT = new CoffeeWorldAction("Right", 0,+1);
    public static CoffeeWorldAction LEFT = new CoffeeWorldAction("Left", 0,-1);
    public static CoffeeWorldAction[] MOVE_ACTIONS = new CoffeeWorldAction[]{UP, DOWN, RIGHT, LEFT};
    public static CoffeeWorldAction DRINK_COFFEE = new CoffeeWorldAction("Drink coffee", +1,+1);

    public String name;
    public int deltaRows;
    public int deltaCols;

    private CoffeeWorldAction(String name, int deltaRows, int deltaCols){
        this.name=name;
        this.deltaRows = deltaRows;
        this.deltaCols = deltaCols;
    }
}
