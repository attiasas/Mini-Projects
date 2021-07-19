package AI_ML_DL.Two_Player_Games.src;

public interface ISolver
{			
	public double 	solve(IBoard board);	
	
	public String	getSolverName();
}
