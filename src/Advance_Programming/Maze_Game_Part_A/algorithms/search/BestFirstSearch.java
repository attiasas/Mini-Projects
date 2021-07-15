package Advance_Programming.Maze_Game_Part_A.algorithms.search;

/**
 * this class implements BestFirst search algorithm
 * Created by Assaf Attias
 */
public class BestFirstSearch extends BreadthFirstSearch
{
    @Override
    protected void applyCost(AState state, double addCost)
    {
        state.setCost(state.getCost() + addCost);
    }

    @Override
    public String getName() {
        return "Best First Search";
    }
}
