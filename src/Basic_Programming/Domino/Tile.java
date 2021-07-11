package Basic_Programming.Domino;

/**
 * This class represent a Tile in the game Domino. Each tile has two values a one on the left and a one on the right.
 * the values is between 0-6
 * @author Assaf Attias
 */
public class Tile 
{
	private int leftNumber;
	private int rightNumber;
	
	/**
	 * Constructor for Tile, initialize the left and right numbers base on a given parameters
	 * @param leftNumber - a value on the left side of the tile (0-6)
	 * @param rightNumber - a value on the right side of the tile (0-6)
	 */
	public Tile(int leftNumber, int rightNumber) 
	{
		this.leftNumber = leftNumber;
		this.rightNumber = rightNumber;
	}
	
	/**
	 * Copy Constructor - initialize as a copy of other Tile
	 * @param other - Tile to copy values from
	 */
	public Tile(Tile other)
	{
		this.leftNumber = other.leftNumber;
		this.rightNumber = other.rightNumber;
	}
	
	/**
	 * returns the value on the left side of the tile
	 * @return - value in the left side
	 */
	public int getLeftNumber() {return leftNumber;}
	
	/**
	 * returns the value on the right side of the tile
	 * @return - value in the right side
	 */
	public int getRightNumber() {return rightNumber;}

	/**
	 *This function flip the tile, the value in the left is switched with the value in the right 
	 */
	public void flipTile() 
	{
		int numberHolder = leftNumber;
		leftNumber = rightNumber;
		rightNumber = numberHolder;
	}
	
	/**
	 * This function returns a string representing the tile 
	 * <Left Value,Right Value>
	 */
	@Override
	public String toString() 
	{
		return "<" + leftNumber + "," + rightNumber + ">";
	}
	
	/**
	 * This Function returns True if a given tile has the same values.
	 */
	@Override
	public boolean equals(Object obj) 
	{
		boolean isEqual = false;
		
		if((obj instanceof Tile))
		{
			if(((Tile) obj).leftNumber == leftNumber && ((Tile) obj).rightNumber == rightNumber)
			{
				isEqual = true;
			}
			else if(((Tile) obj).leftNumber == rightNumber && ((Tile) obj).rightNumber == leftNumber)
			{
				isEqual = true;
			}
		}
		return isEqual;
	}
	
}
