package AI_ML_DL.Two_Player_Games.src;

import java.util.List;

public interface IBoard 
{
	public IBoard copyBoard();
	
	public List<IMove> 		getLegalMoves();
	
	public boolean 			isTheGameOver();
	
	public IBoard getNewChildBoard(IMove move);
	
	public char 			getCurrentPlayer();
	
	public char 			getNextPlayer();
	
	public double			getScore();
	
	public String 			getBoardName();
		
	public void 			printBoard();

}
