package Basic_Programming.Domino;

/**
 * This class represent the game manager of domino, in charge with managing the game and deal tiles to the teams if necessary
 * @author Assaf Attias
 */
public class GameManager 
{
	private Team team1;
	private Team team2;
	private Tile[] tilesInGame;
	private Board board;
	private int numberOfTilesInGame;
	
	/**
	 * Constructor for Game Manager - No need to deal tiles to team
	 * @param team1 - the first team of players
	 * @param team2 - the second team of players
	 */
	public GameManager(Team team1, Team team2)
	{
		this.team1 = new Team(team1);
		this.team2 = new Team(team2);
		numberOfTilesInGame = 28;
		board = new Board(numberOfTilesInGame);
	}
	
	/**
	 * Constructor for Game Manager - manager will deal tiles to the team.
	 * @param team1 - the first team of players
	 * @param team2 - the second team of players
	 * @param tilesPerPlayer - how many tiles to deal to each player
	 */
	public GameManager(Team team1, Team team2, int tilesPerPlayer) 
	{
		this(team1,team2);

		// Initialize Tiles
		tilesInGame = new Tile[28];
		int counterId = 0;
		for(int i = 0; i < 7; i++)
		{
			for(int j = i; j < 7; j++)
			{
				tilesInGame[counterId] = new Tile(i,j);
				counterId++;
			}
		}

		dealTiles(tilesPerPlayer);
	}
	
	/**
	 * This function represents a simulation of a game of Domino
	 * @return - A String with the history of the game
	 */
	public String play() 
	{
		// initialize
		String gameInfo = "";

		boolean gameOver = false;
		
		// check if teams has tiles
		if(!team1.hasMoreTiles() && !team2.hasMoreTiles())
		{
			gameInfo = gameInfo + team1.getName() + ", pass: " + board + "\n" + team2.getName() + ", pass: " + board + "\n";
			gameOver = true;
		}
		else if(team1.hasMoreTiles() && !team2.hasMoreTiles())
		{
			gameInfo = gameInfo + team1.getName();
			if(team1.playMove(board))
			{
				gameInfo = gameInfo + ", success: " + board + "\n";
			}
			else
			{
				gameInfo = gameInfo + ", pass: " + board + "\n";
			}
			gameInfo = gameInfo + team2.getName() + ", pass: " + board + "\n";
			gameOver = true;
		}
		else if(team2.hasMoreTiles() && !team1.hasMoreTiles())
		{
			gameInfo = gameInfo + team1.getName() + ", pass: " + board + "\n";
			gameInfo = gameInfo + team2.getName();
			if(team2.playMove(board))
			{
				gameInfo = gameInfo + ", success: " + board + "\n";
			}
			else
			{
				gameInfo = gameInfo + ", pass: " + board + "\n";
			}
			gameOver = true;
		}

		// play
		while(!gameOver)
		{
			boolean playedMove = false;

			// check if teams has tiles
			if(team1.hasMoreTiles() && team2.hasMoreTiles())
			{
				// team 1 play
				gameInfo = gameInfo + team1.getName();
				if(team1.playMove(board))
				{
					gameInfo = gameInfo + ", success: " + board + "\n";
					playedMove = true;
				}
				else
				{
					gameInfo = gameInfo + ", pass: " + board + "\n";
				}
				
				// team 2 play
				gameInfo = gameInfo + team2.getName();
				if(team2.playMove(board))
				{
					gameInfo = gameInfo + ", success: " + board + "\n";
					playedMove = true;
				}
				else
				{
					gameInfo = gameInfo + ", pass: " + board + "\n";
				}
				
				// check if the game is stuck
				if(!playedMove)
				{
					gameOver = true;
				}
			}
			else
			{
				gameOver = true;
			}
		}
		
		// get score
		gameInfo = gameInfo + team1.getName() + ", score: " + team1.countTiles() + "\n" + team2.getName() + ", score: " + team2.countTiles() + "\n";
		
		// check winner
		if(team1.countTiles() < team2.countTiles())
		{
			gameInfo = gameInfo + team1.getName() + " wins\n";
		}
		else if(team1.countTiles() > team2.countTiles())
		{
			gameInfo = gameInfo + team2.getName() + " wins\n";
		}
		else
		{
			gameInfo = gameInfo + "Draw! - the house wins\n";
		}

		return gameInfo;
	}
	
	/**
	 * This function returns a String with the information on the game
	 */
	@Override
	public String toString() 
	{
		String information = "----- Domino -----\n";
		information = information + "Board: " + board + "\n";
		information = information + team1 + "\n" + team2 + "\n";	
		information = information + "------------------";
		return information;
	}
	
	/**
	 * This function check if a gameManager is equal to another (has the same teams and board)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		boolean isEqual = false;
		
		if((obj instanceof GameManager))
		{
			isEqual = true;
		}
		
		if(isEqual)
		{
			if(!team1.equals(((GameManager)obj).team1) || !team2.equals(((GameManager)obj).team2))
			{
				isEqual = false;
			}
		}
		
		return isEqual;
	}
	
	/**
	 * This function remove a tile from the manager's hand
	 * @param index - the index of the tile in the array that will be removed
	 */
	private void removeTile(int index)
	{
		Tile[] updateTiles = new Tile[tilesInGame.length - 1];
		for(int i = 0; i < tilesInGame.length; i++)
		{
			if(i < index)
			{
				updateTiles[i] = tilesInGame[i];
			}
			else if(i != index)
			{
				updateTiles[i -1] = tilesInGame[i];
			}
		}
		tilesInGame = updateTiles;
	}

	/**
	 * This function will deal the tiles to the teams.
	 * @param numberOfTiles - how many tiles each player will get
	 */
	private void dealTiles(int numberOfTiles) 
	{
		
		Tile[][] tileSetTeam1 = new Tile[team1.getNumberOfPlayers()][];
		Tile[][] tileSetTeam2 = new Tile[team2.getNumberOfPlayers()][];
		
		// Deal Team 1
		for(int i = 0; i < team1.getNumberOfPlayers(); i++)
		{
			Tile[] tilesForPlayer = new Tile[numberOfTiles];
			for(int j = 0; j < numberOfTiles; j++)
			{
				int randomIndex = (int)( Math.random() * tilesInGame.length);
				tilesForPlayer[j] = tilesInGame[randomIndex];
				removeTile(randomIndex);
			}
			tileSetTeam1[i] = tilesForPlayer;
		}
		
		// Deal Team 2
		for(int i = 0; i < team2.getNumberOfPlayers(); i++)
		{
			Tile[] tilesForPlayer = new Tile[numberOfTiles];
			for(int j = 0; j < numberOfTiles; j++)
			{
				int randomIndex = (int)( Math.random() * tilesInGame.length);
				tilesForPlayer[j] = tilesInGame[randomIndex];
				removeTile(randomIndex);
			}
			tileSetTeam2[i] = tilesForPlayer;					
		}
		
		team1.assignTilesToPlayers(tileSetTeam1);
		team2.assignTilesToPlayers(tileSetTeam2);
	}
}
