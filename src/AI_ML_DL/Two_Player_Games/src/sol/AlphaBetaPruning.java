package AI_ML_DL.Two_Player_Games.src.sol;

import java.util.List;

public class AlphaBetaPruning implements ISolver {
    @Override
    public String getSolverName() {
        return "Alpha-Beta Pruning";
    }

    @Override
    public double solve(IBoard board) {
        Node root = new Node(board, Node.NodeType.MAX);
        return AlphaBetaPruningAlgorithm(root, -10000000, 10000000);
    }

    private double AlphaBetaPruningAlgorithm(Node node, double p_alpha, double p_beta) {
        return MaxMin(node,p_alpha,p_beta);
    }

    private double MaxMin(Node maxNode, double p_alpha, double p_beta)
    {
        if(maxNode.isTerminalNode()) return maxNode.getScore();
        double v = -10000000;
        List<Node> successors = maxNode.getNodeChildren();
        for(Node successor : successors)
        {
            v = Math.max(MinMax(successor,p_alpha,p_beta),v);
            p_alpha = Math.max(p_alpha,v);

            if(p_alpha >= p_beta) return v; // Pruning
        }
        return v;
    }

    private double MinMax(Node minNode, double p_alpha, double p_beta)
    {
        if(minNode.isTerminalNode()) return minNode.getScore();
        double v = 10000000;
        List<Node> successors = minNode.getNodeChildren();
        for(Node successor : successors)
        {
            v = Math.min(MaxMin(successor,p_alpha,p_beta),v);
            p_beta = Math.min(p_beta,v);

            if(p_alpha >= p_beta) return v; // Pruning
        }
        return v;
    }

}
