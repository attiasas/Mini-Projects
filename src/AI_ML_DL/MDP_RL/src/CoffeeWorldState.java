package AI_ML_DL.MDP_RL.src;

public class CoffeeWorldState
{
    public int robotAtRow;
    public int robotAtCol;

    public CoffeeWorldState(int robotAtRow, int robotAtCol){
        this.robotAtRow = robotAtRow;
        this.robotAtCol = robotAtCol;
    }


    @Override
    public String toString() {
        return "("+this.robotAtRow+","+this.robotAtCol+")";
    }
}
