package Basic_Programming.Domino;

/**
 * This class represent a board of the game Domino
 * @author Assaf Attias
 */
public class Board 
{
	private int maxTilesOnBoard;
	private int numTilesOnBoard;
	private Tile[] tilesOnBoard;
	
	/**
	 * Constructor for Board
	 * @param numOfTiles - max number of tiles that can be put on the board
	 */
	public Board(int numOfTiles) 
	{
		maxTilesOnBoard = numOfTiles;
		tilesOnBoard = new Tile[maxTilesOnBoard];
		numTilesOnBoard = 0;
	}
	
	/**
	 * Getter for the value in the right side of the board
	 * @return - the value in the right side
	 */
	public int getRightValue() 
	{
		if(numTilesOnBoard == 0)
		{
			return -1;
		}
		else
		{
			return tilesOnBoard[numTilesOnBoard - 1].getRightNumber();
		}
	}
	
	/**
	 * Getter for the value in the left side of the board
	 * @return - the value in the left side
	 */
	public int getLeftValue() 
	{
		if(numTilesOnBoard == 0)
		{
			return -1;
		}
		else
		{
			return tilesOnBoard[0].getLeftNumber();
		}
	}
	
	/**
	 * Getter for an array with the tiles that are on the board
	 * @return - tiles on the board, from left to right (if Empty, Null is returned)
	 */
	public Tile[] getBoard() 
	{
		if(numTilesOnBoard == 0)
		{
			return null;
		}
		else
		{
			Tile[] copyOfTiles = new Tile[numTilesOnBoard];
			for(int i = 0; i < copyOfTiles.length; i ++)
			{
				Tile copyTile = new Tile(tilesOnBoard[i]);
				copyOfTiles[i] = copyTile;
			}
			
			return copyOfTiles;
		}
	}
	
	/**
	 * This function will add a tile on the right side of the board.
	 * @param tile - a new tile to put on the right side of the board
	 * @return - true if the tile was added, false if the tile cannot be put there
	 */
	public boolean addToRightEnd (Tile tile) 
	{
		boolean legalValue = true;
		
		if(tile == null)
		{
			legalValue = false;
		}
		
		if(legalValue)
		{
			if(numTilesOnBoard < maxTilesOnBoard && (getRightValue() == tile.getLeftNumber() || getRightValue() == -1))
			{
				Tile copyTile = new Tile(tile);
				tilesOnBoard[numTilesOnBoard] = copyTile;
				numTilesOnBoard++;
			}
			else if(numTilesOnBoard < maxTilesOnBoard && (getRightValue() == tile.getRightNumber() || getRightValue() == -1))
			{
				Tile copyTile = new Tile(tile);
				copyTile.flipTile();
				tilesOnBoard[numTilesOnBoard] = copyTile;
				numTilesOnBoard++;
			}
			else
			{
				legalValue = false;
			}
		}

		return legalValue;
	}
	
	/**
	 * This function will add a tile on the left side of the board.
	 * @param tile - a new tile to put on the left side of the board
	 * @return - true if the tile was added, false if the tile cannot be put there
	 */
	public boolean addToLeftEnd (Tile tile) 
	{
		boolean legalValue = true;
		
		if(tile == null)
		{
			legalValue = false;
		}
		
		if(legalValue)
		{
			if(numTilesOnBoard < maxTilesOnBoard && (getLeftValue() == tile.getRightNumber() || getLeftValue() == -1))
			{
				Tile copyTile = new Tile(tile);
				// Move tiles to the right if needed
				for(int i = numTilesOnBoard - 1; i >= 0; i--)
				{
					tilesOnBoard[i + 1] = tilesOnBoard[i];
				}
				tilesOnBoard[0] = copyTile;
				numTilesOnBoard++;
			}
			else if(numTilesOnBoard < maxTilesOnBoard && (getLeftValue() == tile.getLeftNumber() || getLeftValue() == -1))
			{
				Tile copyTile = new Tile(tile);
				copyTile.flipTile();
				// Move tiles to the right if needed
				for(int i = numTilesOnBoard - 1; i >= 0; i--)
				{
					tilesOnBoard[i + 1] = tilesOnBoard[i];
				}
				tilesOnBoard[0] = copyTile;
				numTilesOnBoard++;
			}
			else
			{
				legalValue = false;
			}
		}

		return legalValue;
	}
	
	/**
	 * This function returns a string representing the board of the game. (Empty String if the board is empty)
	 */
	@Override
	public String toString() 
	{
		String dominoBoard = "";
		
		for(int i = 0; i < numTilesOnBoard; i ++)
		{
			if(i == numTilesOnBoard - 1)
			{
				dominoBoard = dominoBoard + tilesOnBoard[i];
			}
			else
			{
				dominoBoard = dominoBoard + tilesOnBoard[i] + ",";
			}
		}
		
		return dominoBoard;
	}
	
	/**
	 * This Function returns True if a given board has the same tiles in the same order on the board.
	 */
	@Override
	public boolean equals(Object obj) 
	{
		boolean isEqual = true;
		
		if((obj instanceof Board) && numTilesOnBoard == ((Board)obj).numTilesOnBoard)
		{
			for(int i = 0; i < numTilesOnBoard && isEqual; i++)
			{
				if(tilesOnBoard[i].getLeftNumber() != ((Board)obj).tilesOnBoard[i].getLeftNumber() || tilesOnBoard[i].getRightNumber() != ((Board)obj).tilesOnBoard[i].getRightNumber())
				{
					isEqual = false;
				}
			}
		}
		else
		{
			isEqual = false;
		}
		
		return isEqual;
	}
	
}
