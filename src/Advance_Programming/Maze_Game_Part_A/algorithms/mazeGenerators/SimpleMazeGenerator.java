package Advance_Programming.Maze_Game_Part_A.algorithms.mazeGenerators;

/**
 * This class is a maze generator for simple random mazes
 * Created by Assaf Attias
 */
public class SimpleMazeGenerator extends EmptyMazeGenerator
{
    @Override
    public Maze generate(int rows,int columns)
    {
        // set start and goal
        Maze emptyMaze = super.generate(rows,columns);

        // create random walls
        int[][] maze = new int[rows][columns];

        for(int row = 0; row < maze.length; row++)
        {
            for (int column = 0; column < maze[row].length; column++)
            {
                if(!emptyMaze.getStartPosition().equals(row,column) && !emptyMaze.getGoalPosition().equals(row,column))
                {
                    maze[row][column] = (int)(Math.random() * 2);
                }
            }
        }

        // insure path
        insurePath(emptyMaze.getStartPosition(),emptyMaze.getGoalPosition(),maze);

        emptyMaze.setMaze(maze);
        return emptyMaze;
    }

    /**
     * insure a random path from start to goal
     * @param start - position object
     * @param goal - position object
     * @return list of positions representing the path
     */
    private void insurePath(Position start, Position goal, int[][] maze)
    {
        int rowIndex = start.getRowIndex();
        int columnIndex = start.getColumnIndex();

        while(rowIndex != goal.getRowIndex() || columnIndex != goal.getColumnIndex())
        {
            if(columnIndex != goal.getColumnIndex() && rowIndex != goal.getRowIndex())
            {
                // advance random direction
                if((int)(Math.random() * 2) == 0)
                {
                    if(columnIndex < goal.getColumnIndex()) columnIndex++;
                    else columnIndex--;
                }
                else
                {
                    if(rowIndex < goal.getRowIndex()) rowIndex++;
                    else rowIndex--;
                }
            }
            else if(columnIndex != goal.getColumnIndex())
            {
                // advance column direction
                if(columnIndex < goal.getColumnIndex()) columnIndex++;
                else columnIndex--;
            }
            else
            {
                // advance row direction
                if(rowIndex < goal.getRowIndex()) rowIndex++;
                else rowIndex--;
            }

            // destroy walls
            if(maze[rowIndex][columnIndex] != 0) maze[rowIndex][columnIndex] = 0;
        }
    }
}
