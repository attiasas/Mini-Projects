package Basic_Programming.Domino;

/**
 * This class represents a team of players in the game domino
 * @author Assaf Attias
 */
public class Team 
{
	private Player[] players;
	private String name;
	
	/**
	 * Constructor For Team
	 * @param name - name of the team
	 * @param players - players in the team
	 */
	public Team(String name, Player[] players) 
	{
		this.name = new String(name);
		this.players = new Player[players.length];
		for(int i = 0; i < players.length; i++)
		{
			Player copyPlayer = new Player(players[i]);
			this.players[i] = copyPlayer;
		}
	}
	
	/**
	 * Copy Constructor
	 * @param other - Team to copy from
	 */
	public Team(Team other)
	{
		this.name = new String(other.name);
		this.players = new Player[other.players.length];
		for(int i = 0; i < players.length; i++)
		{
			Player copyPlayer = new Player(other.players[i]);
			this.players[i] = copyPlayer;
		}
	}
	
	/**
	 * This function returns an array with the players on the team
	 * @return - Players in the team, if Empty Null is returned
	 */
	public Player[] getPlayers()
	{
		if(players.length == 0)
		{
			return null;
		}
		else
		{
			Player[] copyPlayers = new Player[players.length];
			
			for(int i = 0; i < players.length; i++)
			{
				Player copyPlayer = new Player(players[i]);
				copyPlayers[i] = copyPlayer;
			}
			
			return copyPlayers;
		}

	}
	
	/**
	 * Getter for the name of the team
	 * @return - the name of the team
	 */
	public String getName()
	{
		String CopyName = new String(name);
		
		return CopyName;
	}
	
	/**
	 * This function represent a move of a team in the game, by rotation, each player in his turn will try to play his tile
	 * if he can't, the turn is passed to the the player after him, if no one can play false is returned
	 * @param board - Domino Board
	 * @return - true if a move was played.
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
			for(int i = 0; i < players.length && !movePlayed; i++)
			{
				movePlayed = players[i].playMove(board);
			}
		}
		return movePlayed;
	}
	
	/**
	 * Count the number of points the team have, base on the tiles each player have
	 * @return - the number of points base on tile
	 */
	public int countTiles()
	{
		int score = 0;
		
		for(int i = 0; i < players.length; i ++)
		{
			score = score + players[i].countTiles();
		}
		
		return score;
	}
	
	/**
	 * This function check if all the members of the team still have tiles in their hands.
	 * @return true - if at lest one player have tiles, else false
	 */
	public boolean hasMoreTiles()
	{
		boolean haveTiles = false;
		
		for(int i = 0; i < players.length && !haveTiles; i ++)
		{
			haveTiles = players[i].hasMoreTiles();
		}
		
		return haveTiles;
	}
	
	/**
	 * This function count how many players are in the team
	 * @return - number of players in the team
	 */
	public int getNumberOfPlayers()
	{
		return players.length;
	}
	
	/**
	 * This function assign Tiles to the players in the team
	 * @param allHands - an array of tiles for each player
	 * @return - true if success
	 */
	public boolean assignTilesToPlayers(Tile[][] allHands)
	{
		boolean legalInput = true;
		
		if(allHands == null)
		{
			legalInput = false;
		}
		if(legalInput)
		{
			if(allHands.length != players.length)
			{
				legalInput = false;
			}
			
			for(int i = 0; i < allHands.length && legalInput; i++)
			{
				if(allHands[i] == null)
				{
					legalInput = false;
				}
				else
				{
					for(int j = 0; j < allHands[i].length && legalInput; j++)
					{
						if(allHands[i][j] == null)
						{
							legalInput = false;
						}
					}
				}
			}
		}
			
		if(legalInput)
		{
			for(int i = 0; i < players.length && legalInput; i++)
			{
				legalInput = players[i].assignTiles(allHands[i]);
			}
		}
		
		return legalInput;
	}
	
	/**
	 * This Function Returns a String representing of the Team
	 */
	@Override
	public String toString() 
	{
		String team = "Team: " + name + " {";
		
		if(players.length == 0)
		{
			team = team + "No players in the team}";
		}
		else
		{
			for(int i = 0; i < players.length; i ++)
			{
				if(i == players.length - 1)
				{
					team = team + players[i] + "}";
				}
				else
				{
					team = team + players[i] + ",";
				}
			}
		}
		
		return team;
	}
	
	/**
	 * This function check if the team is the same as a given team (base on players and name)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		boolean isEqual = false;
		
		if((obj instanceof Team) && name.equals(((Team)obj).name) && players.length == ((Team)obj).players.length)
		{
			isEqual = true;
			
		}
		
		if(isEqual)
		{
			for(int i = 0; i < players.length && isEqual; i++)
			{
				if(!players[i].equals(((Team)obj).players[i]))
				{
					isEqual = false;
				}
			}
		}
		
		return isEqual;
	}
}
