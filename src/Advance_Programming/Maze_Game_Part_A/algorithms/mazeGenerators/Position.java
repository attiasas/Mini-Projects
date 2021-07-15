package Advance_Programming.Maze_Game_Part_A.algorithms.mazeGenerators;

/**
 * This class represents a position inside a maze
 * Created by Assaf Attias
 */
public class Position
{
    private int rowIndex;
    private int columnIndex;

    /**
     * Parameters Constructor
     * @param rowIndex - position row index in the maze ( rowIndex >= 0 )
     * @param columnIndex - position column index in the maze ( columnIndex >= 0 )
     */
    public Position(int rowIndex, int columnIndex)
    {
        // validate
        if(rowIndex < 0) rowIndex = 0;
        if(columnIndex < 0) columnIndex = 0;

        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    /**
     * Copy Constructor
     * @param other - position to copy from
     */
    public Position(Position other)
    {
        this.rowIndex = other.rowIndex;
        this.columnIndex = other.columnIndex;
    }

    /**
     * Getter for the position row index
     * @return - row index of position inside the maze
     */
    public int getRowIndex() { return rowIndex; }

    /**
     * Getter for the position column index
     * @return - column index of position inside the maze
     */
    public int getColumnIndex() { return columnIndex; }

    /**
     * check if the given coordinates matches the position
     * @param row - row coordinate
     * @param column- column coordinate
     * @return - true if match
     */
    public boolean equals(int row, int column)
    {
        return row == rowIndex && column == columnIndex;
    }

    @Override
    public String toString()
    {
        return "{" + rowIndex + "," + columnIndex + "}";
    }

    @Override
    public int hashCode() { return toString().hashCode(); }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Position)) return false;
        Position other = (Position)obj;

        return equals(other.rowIndex,other.columnIndex);
    }

}
