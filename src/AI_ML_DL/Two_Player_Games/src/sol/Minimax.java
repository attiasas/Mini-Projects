package AI_ML_DL.Two_Player_Games.src.sol;

import java.util.List;

public class Minimax implements ISolver {
    @Override
    public String getSolverName() {
        return "Minimax";
    }

    @Override
    public double solve(IBoard board) {
        Node root = new Node(board, Node.NodeType.MAX);
        return MinimaxAlgorithm(root);
    }


    private double MinimaxAlgorithm(Node node)
    {
        return MaxMin(node);
    }


    private double MaxMin(Node maxNode)
    {
        if(maxNode.isTerminalNode()) return maxNode.getScore();
        double v = -Double.MAX_VALUE;
        List<Node> successors = maxNode.getNodeChildren();
        for(Node successor : successors)
        {
            v = Math.max(v,MinMax(successor));
        }
        return v;
    }

    private double MinMax(Node minNode)
    {
        if(minNode.isTerminalNode()) return minNode.getScore();
        double v = Double.MAX_VALUE;
        List<Node> successors = minNode.getNodeChildren();
        for(Node successor : successors)
        {
            v = Math.min(v, MaxMin(successor));
        }
        return v;
    }
}
