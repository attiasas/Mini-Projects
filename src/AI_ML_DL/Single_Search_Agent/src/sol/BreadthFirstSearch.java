package AI_ML_DL.Single_Search_Agent.src.sol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class BreadthFirstSearch  extends ASearch
{
	// Define lists here ...
	private Queue<ASearchNode> openList;

	private HashSet<ASearchNode> closeList;
	private HashMap<Integer,ASearchNode> openLinkList;
	
	@Override
	public String getSolverName() 
	{
		return "BFS";
	}

	@Override
	public ASearchNode createSearchRoot
	(
		TopSpinPuzzleState problemState
	) 
	{
		ASearchNode newNode = new BlindSearchNode(problemState);
		return newNode;
	}
	
	@Override
	public void initLists() 
	{
		openList = new LinkedList<>();
		closeList = new HashSet<>();
		openLinkList = new HashMap<>();
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
		if(openLinkList.containsKey(node.hashCode())) return;
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