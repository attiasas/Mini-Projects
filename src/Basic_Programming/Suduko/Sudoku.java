package Basic_Programming.Suduko;// Author: Assaf Attias

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class represent a sudoku game. it will recive and read a sudoku board, dicide if the board is solvable and if it is
 * it cam print the solution for the board.
 * @author Assaf Attias
 *
 */
public class Sudoku {

	public static final int SOLVED = 0;
	public static final int UNSOLVED = 1;
	public static final int UNSOLVABLE = 2;

	public static void main(String[] args) {

		int[][] board = readBoardFromFile(".\\src\\Basic_Programming\\Suduko\\S1.txt");
		
	}
	
	
	// **************   Sudoku - Read Board From Input File   **************
	public static int[][] readBoardFromFile(String fileToRead){
		int[][] board = new int[9][9];
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToRead)); // change S1.txt to any file you like (S2.txt, ...)
			int row = 0;
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				for(int column = 0; column < line.length(); column++){
					char c = line.charAt(column);
					if(c == 'X')
						board[row][column] = 0;
					else board[row][column] = c - '0';
				}
				row++;
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return board;
	}
	
	// **************   Sudoku - Part1 (iterative)   **************
	/**
	 * This function check how many options can be assign to each square in a sudoko board and if a square has only one option
	 * to assign the function will assign it to the board and keep checking from the beginning. 
	 * @param board - the sudoku board
	 * @return an array with all the options
	 * left to assign (or the number that is assign to the square).
	 */
	public static int[][][] eliminateDomains(int[][] board){
		int[][][] domains = new int[board.length][board[0].length][];
		boolean eliminatedNumber = true;
		
		while(eliminatedNumber)
		{
			eliminatedNumber = false;
			for(int row = 0; row < board.length; row++)
			{
				for(int col = 0; col < board[row].length; col++)
				{
					if(board[row][col] == 0)
					{
						int[] options = checkOptions(board, row, col);
						if(options.length == 1)
						{
							board[row][col] = options[0];
							eliminatedNumber = true;
						}
						domains[row][col] = options;
					}
					else
					{
						int[] numberInPlace = new int[1];
						numberInPlace[0] = board[row][col];
						domains[row][col] = numberInPlace;
					}
				}
			}
		}

		return domains;
	}
	 
	/**
	 * This Function calculates the options that can be assigned to a given squre in the board
	 * @param board - the sudoku board
	 * @param row - index of the row to check
	 * @param col - index of the column to check
	 * @return an int array with the options (numbers) to assign.
	 */
	public static int[] checkOptions(int[][]board, int row, int col)
	{
		int[] options;
		int[] digits = new int[9];
		for(int i = 0; i < digits.length; i++)
		{
			digits[i] = 1;
		}
		int groupBoxRowIndex = row / 3;
		int groupBoxColIndex = col / 3;
		groupBoxRowIndex = groupBoxRowIndex * 3;
		groupBoxColIndex = groupBoxColIndex * 3;

		// Eliminate the numbers in the same box (3X3) of [row][col]
		for(int i = groupBoxRowIndex; i < groupBoxRowIndex + 3; i++)
		{
			for(int j = groupBoxColIndex; j < groupBoxColIndex + 3; j++)
			{
				if(board[i][j] != 0)
				{
					digits[board[i][j] - 1] = -1;
				}
			}
		}
		// Eliminate the same Row and Col
		for(int i = 0; i < board.length; i++)
		{
			if(board[i][col] != 0)
			{
				digits[board[i][col]-1] = -1;
			}
		}
		for(int i = 0; i < board[0].length; i++)
		{
			if(board[row][i] != 0)
			{
				digits[board[row][i]-1] = -1;
			}
		}
		
		int numberOfOptions = 0;
		for(int i = 0; i < digits.length; i++)
		{
			if(digits[i] == 1)
			{
				numberOfOptions++;
			}
		}
		
		options = new int[numberOfOptions];
		int indexOptions = 0;
		for(int i = 0; i < digits.length; i++)
		{
			if(digits[i] == 1 && indexOptions < numberOfOptions)
			{
				options[indexOptions] = i + 1;
				indexOptions ++;
			}
		}

		return options;
	}
	
	/**
	 * This function print to the console the boardof the sudoku and after that prints all the options to each square 
	 * (if a number is assing to it it will print that number).
	 * @param domains - all the options for the board
	 * @param board - the board of the sudoku
	 */
	public static void printBoard(int[][][] domains, int[][] board){
		// print board
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[0].length; j++)
			{
				System.out.print(board[i][j]);
				if(j % 3 == 2 && j < board[0].length - 1)
				{
					System.out.print("|");
				}
			}
			System.out.println();
			if(i % 3 == 2 && i < board.length - 1)
			{
				System.out.println("---+---+---");
			}
			
		}
		
		// print options
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[0].length; j++)
			{
				System.out.print(i + "," + j + " = ");
				for(int k = 0; k < domains[i][j].length; k++)
				{
					System.out.print(domains[i][j][k] + ",");
				}
				System.out.println();
			}
		}
	}
	
	/**
	 * This function check if the sudoku is solved or not. and if not is the board Solvable.
	 * @param domains
	 * @return - SOLVED (0) if solved.
	 * UNSOLVED (1) if not solved.
	 * UNSOLVABLE (2) if the board can't be solved.
	 */
	public static int isSolved(int[][][] domains, int[][] board)
	{
		int solved = SOLVED;
		for(int i = 0; i < domains.length && solved == SOLVED; i++)
		{
			for(int j = 0; j < domains.length && solved == SOLVED; j++)
			{
				if(domains[i][j].length == 0)
				{
					solved = UNSOLVABLE;
				}
				else if(domains[i][j].length > 1)
				{
					solved = UNSOLVED;
				}
				
				// check if a number appear in 2 places illegally
				for(int row = i + 1; row < board.length && solved == SOLVED; row++)
				{
					if(board[i][j] == board[row][j]) solved = UNSOLVABLE;
				}
				for(int col = j + 1; col < board.length && solved == SOLVED; col++)
				{
					if(board[i][j] == board[i][col]) solved = UNSOLVABLE;
				}
			}
		}
		
		return solved;
	}
	
	// **************   Sudoku - Part2 (recursive)   **************
	
	/**
	 * This function creates and return a copy of the board.
	 * @param board - sudoku board
	 * @return copy of the sudoku board
	 */
	public static int[][] copyBoard(int[][] board)
	{
		int[][] newBoard = new int[board.length][board.length];
		
		for(int row = 0; row < board.length; row++)
		{
			for(int col = 0; col < board.length; col++)
			{
				newBoard[row][col] = board[row][col];
			}
		}
		
		return newBoard;
	}
	
	/**
	 * This function check if a given sudoku board is solveable or not.
	 * @param board - sudoku board
	 * @return true if the board is solvable
	 */
	public static boolean solveSudoku(int[][] board)
	{
		int[][] copyBoard = copyBoard(board);
		int[][][] domains = eliminateDomains(copyBoard);
		boolean solved = false;
		
		if(isSolved(domains,copyBoard) == SOLVED)
		{
			solved = true;
		}
		else if(isSolved(domains,copyBoard) == UNSOLVABLE)
		{
			solved = false;
		}
		else
		{
			for(int row = 0; row < domains.length && !solved; row ++)
			{
				for(int col = 0; col < domains.length && !solved; col++)
				{
					if(domains[row][col].length > 1)
					{
						for(int i = 0; i < domains[row][col].length && !solved; i++)
						{
							copyBoard[row][col] = domains[row][col][i];
							solved = solveSudoku(copyBoard);
						}
					}
				}
			}
			
		}
		return solved;
	}
	
}






