package Basic_Programming.Domino;

/**
 * This class represents a player in the game of Domino
 * @author Assaf Attias
 */
public class Player 
{
	private String name;
	private Tile[] tileSet;
	
	/**
	 * Constructor for Player
	 * @param name - name of the player
	 * @param tiles - the given tiles the player has in the game.
	 */
	public Player(String name, Tile[] tiles) 
	{
		this.name = new String(name);
		tileSet = new Tile[tiles.length];
		for(int i = 0; i < tileSet.length; i++)
		{
			Tile copyTile = new Tile(tiles[i]);
			tileSet[i] = copyTile;
		}
	}
	
	/**
	 * Constructor for Player
	 * @param name - name of the player
	 */
	public Player(String name) 
	{
		this.name = new String(name);
		tileSet = new Tile[0];
	}
	
	/**
	 * Copy Constructor for player
	 * @param player - A Player to copy from
	 */
	public Player(Player player)
	{
		this.name = new String(player.name);
		this.tileSet = new Tile[player.tileSet.length];
		
		for(int i = 0; i < tileSet.length; i++)
		{
			Tile copyTile = new Tile(player.tileSet[i]);
			tileSet[i] = copyTile;
		}
	}
	
	/**
	 * This function changes the tile Set the player has with the given tileSet
	 * @param tiles - a set of tiles 
	 * @return true if success or false if not 
	 */
	public boolean assignTiles(Tile[] tiles) 
	{
		boolean legalInput = true;
		// check Tile Set
		if(tiles == null)
		{
			legalInput = false;
		}
		else
		{
			if(tiles.length > 28)
			{
				legalInput = false;
			}
			
			for(int i = 0; i < tiles.length && legalInput; i++)
			{
				if(tiles[i] == null)
				{
					legalInput = false;
				}
			}
		}
		
		if(legalInput)
		{
			Tile[] copyTileSet = new Tile[tiles.length];
			for(int i = 0; i < copyTileSet.length; i++)
			{
				Tile copyTile = new Tile(tiles[i]);
				copyTileSet[i] = copyTile;
			}
			tileSet = copyTileSet;
		}
		
		return legalInput;
	}
	
	/**
	 * This function remove a tile from the Tile Set
	 * @param removeIndex - the index of the tile that will be removed
	 */
	private void removeTile(int removeIndex)
	{
		Tile[] newTileSet = new Tile[tileSet.length - 1];
		for(int i = 0; i < tileSet.length; i++)
		{
			if(i < removeIndex)
			{
				newTileSet[i] = tileSet[i];
			}
			else if(i != removeIndex)
			{
				newTileSet[i -1] = tileSet[i];
			}
		}
		tileSet = newTileSet;
	}
	
	/**
	 * This function returns the index of the tile, that is best move to play 
	 * @param value - the value that the domino tile needs to match, if -1 it will play the best value he has
	 * @return - index of the tile in tileSet, if there is no move to play it will return -1
	 */
	private int getBestOption(int value)
	{
		int index = -1;
		int bestValue = -1;
		
		for(int i = 0; i < tileSet.length; i++)
		{
			if(tileSet[i].getLeftNumber() == value || tileSet[i].getRightNumber() == value || value == -1)
			{
				if((tileSet[i].getLeftNumber() + tileSet[i].getRightNumber()) > bestValue)
				{
					index = i;
					bestValue = tileSet[i].getLeftNumber() + tileSet[i].getRightNumber();
				}
			}
		}
		
		return index;
	}
	
	/**
	 * This function represents a move in the game, the player will assign a tile to the board if he can
	 * @param board - domino board
	 * @return true if the move is success false if the player has no moves
	 */
	public boolean playMove(Board board) 
	{
		boolean legalValue = true;
		boolean movePlayed = false;
		
		if(board == null)
		{
			legalValue = false;
		}
		
		if(legalValue)
		{
			if(tileSet.length != 0)
			{
				if(board.getLeftValue() > board.getRightValue())
				{
					int bestMoveIndex = getBestOption(board.getLeftValue());
					if(bestMoveIndex != -1)
					{
						movePlayed = board.addToLeftEnd(tileSet[bestMoveIndex]);
						// update tileSet
						if(movePlayed)
						{
							removeTile(bestMoveIndex);
						}
					}
				}
				
				if(!movePlayed)
				{
					int bestMoveIndex = getBestOption(board.getRightValue());
					if(bestMoveIndex != -1)
					{
						movePlayed = board.addToRightEnd(tileSet[bestMoveIndex]);
						// update tileSet
						if(movePlayed)
						{
							removeTile(bestMoveIndex);
						}
					}
					else
					{
						bestMoveIndex = getBestOption(board.getLeftValue());
						if(bestMoveIndex != -1)
						{
							movePlayed = board.addToLeftEnd(tileSet[bestMoveIndex]);
							// update tileSet
							if(movePlayed)
							{
								removeTile(bestMoveIndex);
							}
						}
					}
				}
			}
		}
		
		return movePlayed;
	}
	
	/**
	 * This function returns the total number of points the player has (base on the tiles he holds)
	 * @return - the number of points the player has
	 */
	public int countTiles() 
	{
		int score = 0;
		
		for(int i = 0; i < tileSet.length; i++)
		{
			score = score + tileSet[i].getLeftNumber() + tileSet[i].getRightNumber(); 
		}
		
		return score;
	}
	
	/**
	 * This function returns true if the player has tiles in his hands and false if he does not.
	 * @return - true if he have tiles.
	 */
	public boolean hasMoreTiles() 
	{
		if(tileSet.length == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * This function will return a string representing the player in the format - Name:[Player Tile Set]
	 */
	@Override
	public String toString() 
	{
		String player = name + ":[";
		for(int i = 0; i < tileSet.length; i++)
		{
			if(i == tileSet.length - 1)
			{
				player = player + tileSet[i];
			}
			else
			{
				player = player + tileSet[i] + ",";
			}
		}
		player = player + "]";
		
		return player;
	}
	
	/**
	 * This function check if a given player is equal to the current (Equals have the same name and tileSet)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		boolean isEqual = false;
		if((obj instanceof Player))
		{
			if(((Player)obj).name.equals(this.name) && ((Player)obj).tileSet.length == this.tileSet.length)
			{
				isEqual = true;
			}
			
			for(int i = 0; i < tileSet.length && isEqual; i++)
			{
				boolean haveTile = false;
				for(int j = 0; j < tileSet.length && !haveTile; j++)
				{
					if(tileSet[i].equals(((Player)obj).tileSet[j]))
					{
						haveTile = true;
					}
				}
				
				if(!haveTile)
				{
					isEqual = false;
				}
			}
		}
		
		return isEqual;
	}
}
