package AI_ML_DL.Single_Search_Agent.src.sol;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class AStarSearch   extends ASearch
{
	// Define lists here ...
	private PriorityQueue<ASearchNode> openList;
	private HashSet<ASearchNode> closeList;
	private HashMap<Integer,ASearchNode> openLinkList;
	
	@Override
	public String getSolverName() 
	{
		return "AStar";
	}
	
	@Override
	public ASearchNode createSearchRoot
	(
		TopSpinPuzzleState problemState
	) 
	{	
		ASearchNode newNode = new HeuristicSearchNode(problemState);
		return newNode;
	}

	@Override
	public void initLists() 
	{
		openList = new PriorityQueue<>(Comparator.comparingDouble(ASearchNode::F).thenComparingDouble(ASearchNode::H));
		openLinkList = new HashMap<>();
		closeList = new HashSet<>();
	}

	@Override
	public ASearchNode getOpen
	(
		ASearchNode node
	) 
	{
		return openLinkList.get(node);
	}

	@Override
	public boolean isOpen
	(
		ASearchNode node
	) 
	{
		return openLinkList.containsKey(node);
	}
	
	@Override
	public boolean isClosed
	(
		ASearchNode node
	) 
	{
		return closeList.contains(node);
	}

	@Override
	public void addToOpen
	(
		ASearchNode node
	) 
	{
		openList.offer(node);
		openLinkList.put(node.hashCode(),node);
	}

	@Override
	public void addToClosed
	(
		ASearchNode node
	) 
	{
		if(closeList.contains(node)) return;
		closeList.add(node);
	}

	@Override
	public int openSize() 
	{
		return openList.size();
	}

	@Override
	public ASearchNode getBest() 
	{
		ASearchNode best = openList.poll();
		openLinkList.remove(best);
		return best;
	}

}
