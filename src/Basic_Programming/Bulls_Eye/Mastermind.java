package Basic_Programming.Bulls_Eye;

/*
 * this program simulates a game of MasterMind, a random secret is generated every time, and the program will try to
 * guess the number. in each round the program will recive a score of HITS and FITS and will try a next guess base on the score.
*/
public class Mastermind
{
	public static int N = 4; 					// Number of digits in a tuple
	public static int BASE = 10;				// Base of the digits
	public static int MAX_ROUND_NUMBER = 6;		// Number of rounds in a game

	public static void main(String[] args) {
		Frame.play();
		
		
		

		// TO TEST YOUR CODE MARK THE LINE  "Frame.play();"  AS A REMRAK AND UNMARK THE REQUESTED TEST:


		/*  +++++++++++++++++++++++++ test for areArraysEqual +++++++++++++++++++++++++++++++

		int[]arr1 = {1,2,3,9};
		int[]arr2 = {1,2,3,9};
		int[]arr3 = {1,2,4,0};
		int[]arr4 = {1,2};
		int[]arr5 = null;
		System.out.println(areArraysEqual(arr1,arr2));
		System.out.println(areArraysEqual(arr1, arr3));
		System.out.println(areArraysEqual(arr1, arr4));
		System.out.println(areArraysEqual(arr1, arr5));

		// expected output: true
		//				    false
		// 			 	    false

		*/

		/*  +++++++++++++++++++++++++ test for randomizeSequence +++++++++++++++++++++++++++++++

		for(int j = 0; j < 11; j++)
		{
			int[] arr = randomizeSequence();
			for(int i = 0; i < arr.length; i++)
			{
				System.out.print("| " + arr[i] + " |");
			}
			boolean b = false;
			for(int i = 0; i < arr.length; i++)
			{
				for(int k = i + 1; k <= arr.length - 1; k++)
				{
					if(arr[k] == arr[i]) b = true;
				}
			}
			System.out.print(" " + b);
			System.out.println();
		}

		*/

		/*  +++++++++++++++++++++++++ test for incrementOdometer ++++++++++++++++++++++++++++++

		int[]arr5 = {0,1,2,1,2};
		int[]arr6 = null;
		
		arr6 = incrementOdometer(arr5);
		for (int i = 0; i < arr6.length; i++) {
			System.out.print(arr6[i]+ " ");
		}
		System.out.println();

		// expected output: 0 1 0 0

		*/



		/*  +++++++++++++++++++++++++ test for isRightfulGuess +++++++++++++++++++++++++++

		int[]arr7 = {0,2,3,9};
		int[]arr8 = {2,1,3,2};
		int[]arr9 = {2,1,3};
		int[]arr10 = {2,1,3,0};
		int[]arr11 = {2,1,3,10};
		
		System.out.println(isRightfulGuess(arr7)); // true
		System.out.println(isRightfulGuess(arr8)); // false
		System.out.println(isRightfulGuess(arr9)); // false
		System.out.println(isRightfulGuess(arr10)); // true
		System.out.println(isRightfulGuess(arr11)); // false

		// expected output: true
		//			 	    false

		*/



		/* +++++++++++++++++++++++++ test for getNextRightfulGuess +++++++++++++++++++++++++

		int[] arr2 = {1,2,3,0};
		int[] arr1 = getNextRightfulGuess(arr2);
		for (int i = 0; i < arr1.length; i++) {
			System.out.print(arr1[i]+ " ");
		}
		System.out.println();
		for (int i = 0; i < arr2.length; i++) {
			System.out.print(arr2[i]+ " ");
		}
		System.out.println();

		// expected output: 1 2 4 0

		*/



		/*  +++++++++++++++++++++++++ test for judgeBetweenGuesses +++++++++++++++++++++++++++++++++++++

		int[] guess1 = {5,1,0,3};
		int[] guess2 = {4,1,3,2};
		int[] hitFitAnswer = judgeBetweenGuesses(guess1, guess2);
		System.out.print(hitFitAnswer[0] + " " + hitFitAnswer[1]);
		System.out.println();

		// expected output: 2 2

		*/
		
		/*  +++++++++++++++++++++++++ test for settleGuessInHistory +++++++++++++++++++++++++++++++++++++

				int[][][] histo = {{{0,1,2,3},{1,0}},{{0,4,5,6},{0,3}},{{4,1,6,5},{0,3}}};
				
				int[] currentGuess = {4,1,6,7};
				int[] currentGuess2 = {0,4,5,3};
				int[] currentGuess3 = {4,5,6,3};
				int[] currentGuess4 = {6,5,4,3};
				int[] currentGuess5 = {5,6,2,4};
				
				System.out.println(settleGuessInHistory(3, currentGuess, histo));
				System.out.println(settleGuessInHistory(3, currentGuess2, histo));
				System.out.println(settleGuessInHistory(3, currentGuess3, histo));
				System.out.println(settleGuessInHistory(3, currentGuess4, histo));
				System.out.println(settleGuessInHistory(3, currentGuess5, histo));

				*/


		/*  +++++++++++++++++++++++++ test for printGame and play +++++++++++++++++++++++++

		int[] arr1 = {1,2,3,4};
		printGame(arr1, play(arr1));

		//expected output:  the secret is 1 2 3 4
		//			  		the guess 0 1 2 3  gives (h/f): 0 3
		//			  		the guess 1 0 3 4  gives (h/f): 3 0
		//			 		 the guess 1 0 3 5  gives (h/f): 2 0
		//			 		 the guess 1 2 3 4  gives (h/f): 4 0
		//			 		 you guessed the secret within 4  rounds

		*/


	} // end of main




	/**
	 * this function returns True only if the two Arrays has the same size and the same values in all of their index.
	 * {1,1,1} {1,0,1} ---> False, 
	 * {1,1} {1,1,1} -----> False, 
	 * {2,1} {1,2} -------> Fasle, 
	 * {2,1} {2,1} -------> True
	 **/
	public static boolean areArraysEqual(int[] firstArr, int[] secArr) {
		boolean equal = true;
		
		if(firstArr != null && secArr != null && firstArr.length == secArr.length)
		{
			for(int i = 0; i < firstArr.length; i++)
			{
				if(firstArr[i] != secArr[i]) equal = false;
			}
		}
		else
		{
			equal = false;
		}
		return equal;
	}

	/**
	 * this function creates a new Array (N in size) and randomize the values of the array, 
	 *all the values will be different and are less than BASE in value
	 **/
	public static int[] randomizeSequence() {
		int[] array = new int[N];
		int randomInt = (int)(Math.random() * BASE);
		array[0] = randomInt;
		for(int i = 1; i < array.length; i++)
		{
			boolean newNumber = false;
			
			while(!newNumber)
			{
				newNumber = true;
				randomInt = (int)(Math.random() * BASE);
				for(int j = i; j >= 0; j--)
				{
					if(array[j] == randomInt)
					{
						newNumber = false;
					}
				}
			}
			array[i] = randomInt;
		}
		
		return array;	
	}

	/**
	 * Receive an array that his values representing a number (in the base of BASE), increasing that number by 1
	 * and returning a new array with values representing the new number
	 **/
	public static int[] incrementOdometer(int[] odometer) {
		int[] increasedOdometer = new int[odometer.length];
		for(int j = 0; j < increasedOdometer.length; j++)
		{
			increasedOdometer[j] = odometer[j];
		}
		
		int i = increasedOdometer.length - 1;
		
		while(i >= 0 && increasedOdometer[i] == BASE - 1)
		{
			increasedOdometer[i] = 0;
			i --;
		}
		if(i >= 0)
		{
			increasedOdometer[i] += 1;
		}

		return increasedOdometer;
	}

	/**
	 * check if an Array contains a legal guess (if the Array is N in size and does not contain duplicate values, and the values are in base BASE).
	 *
	 **/
	public static boolean isRightfulGuess(int[] guessArray) {
		boolean equal = true;
		
		if(guessArray.length == N)
		{
			for(int i = 0; i < guessArray.length; i++)
			{
				if(guessArray[i] >= BASE)
				{
					equal = false;
				}
				for(int j = i + 1; j <= guessArray.length - 1; j++)
				{
					if(guessArray[i] == guessArray[j])
					{
						equal = false;
					}
				}
				
			}
		}
		else
		{
			equal = false;
		}
		
		return equal;	// Change
	}

	/**
	 * Receive a guess and returns the next legal guess (a guess that's not contains duplicate values and in the base BASE) 
	 *
	 **/
	public static int[] getNextRightfulGuess(int[] guessArray) {
		boolean goodGuess = false;
		int[] newGuessArray = guessArray;
		
		while(!goodGuess)
		{
			goodGuess = true;
			newGuessArray = incrementOdometer(newGuessArray);
			
			for(int i = 0; i < newGuessArray.length - 1; i++)
			{
				for(int j = i + 1; j < newGuessArray.length; j++)
				{
					if(newGuessArray[i] == newGuessArray[j])
					{
						goodGuess = false;
					}
				}
			}
		}
		
		
		return newGuessArray;	// Change
	}


	/**
	 * judge the number of hits and fits between two guesses, and returns an array with the number of hits and fits. [HIT,FITS]
	 *
	 **/
	public static int[] judgeBetweenGuesses(int[] guess1, int[] guess2) {
		int[] answer = new int[2];
		
		for(int i = 0; i < guess1.length; i++)
		{
			for(int j = 0; j < guess1.length; j++)
			{
				if(i == j)
				{
					if(guess1[i] == guess2[i]) // HIT
					{
						answer[0] ++;
					}
				}
				else
				{
					if(guess1[i] == guess2[j]) // FIT
					{
						answer[1] ++;
					}	
				}
			}
		}
		
		return answer;	// Change
	}

	/**
	 * check to see if a guess fits the same answers as the previous rounds, if it does, the guess is legal 
	 *and the function returns true
	 **/
	public static boolean settleGuessInHistory(int round, int[] currentGuess, int[][][] gameHistory) {
		boolean goodGuess = true;
		
		for(int i = 0; i < round; i++)
		{
			int[] currentJudge = judgeBetweenGuesses(currentGuess, gameHistory[i][0]);
			if(currentJudge[0] != gameHistory[i][1][0] || currentJudge[1] != gameHistory[i][1][1])
			{
				goodGuess = false;
			}
		}
		
		return goodGuess;	
	}

	/**
	 * adds a newGuess and answer (hits/fits) to the gameHistory at "line" round. We assume that round < MAX_ROUND_NUMBER.
	 **/
	public static void update(int[][][] gameHistory, int round, int[] newGuess, int[] score) {
		gameHistory[round][0] = newGuess;
		gameHistory[round][1] = score;
	}  

	/**
	 * simulate a game of Mastermind, trying to guess the secret within the number of turns allowed.
	 * and returning the history of the game in an array.
	 **/
	public static int[][][] play(int[] secret) {

		boolean found = false;
		int[][][] gameHistory = new int[MAX_ROUND_NUMBER][2][];
		int[] currentGuessArray = new int[N];

		for (int i=0; i<N; i=i+1) {
			currentGuessArray[i] = 0;
		}

		int round = 0;
		while (round < MAX_ROUND_NUMBER && !found) {
			// ENTER YOUR CODE HERE
			// get the next good guess
			if(round == 0)
			{
				currentGuessArray = getNextRightfulGuess(currentGuessArray);
			}
			while(!settleGuessInHistory(round, currentGuessArray, gameHistory))
			{
				currentGuessArray = getNextRightfulGuess(currentGuessArray);
			}
			
			// check if the guess is the secret
			if(judgeBetweenGuesses(currentGuessArray, secret)[0] == N)
			{
				found = true;
			}
			
			//get score and update history
			update(gameHistory, round, currentGuessArray, judgeBetweenGuesses(currentGuessArray, secret));
			
			round++;
		}
		
		return gameHistory;
	}

	/**
	 * this functions prints the history of the game to the console and the
	 * Conclusion of the game (if its a win or a lose) 
	 **/
	public static void printGame(int[] secret, int[][][] gameHistory) {
		boolean win = false;
		int rounds = 0;
		
		// check if the game is a win and the number of Rounds it took
		for(int i = 0; i < gameHistory.length; i++)
		{
			if(areArraysEqual(secret, gameHistory[i][0]))
			{
				win = true;
			}
			if(gameHistory[i][0] != null)
			{
				rounds ++;
			}
		}
		
		// Prints secret
		System.out.print("The secret is ");
		for(int i = 0; i < secret.length; i++)
		{
			System.out.print(secret[i] + " ");
		}
		System.out.println();
		// Prints Guesses 
		for(int i = 0; i < rounds; i++)
		{
			System.out.print("The guess ");
			for(int j = 0; j < gameHistory[i][0].length; j++)
			{
				System.out.print(gameHistory[i][0][j] + " ");
			}
			System.out.print("gives (h/f): ");
			for(int j = 0; j < gameHistory[i][1].length; j++)
			{
				System.out.print(gameHistory[i][1][j] + " ");
			}
			System.out.println();
		}
		// Prints Conclusion
		if(win)
		{
			System.out.print("You guessed the secret within " + rounds + " rounds");
		}
		else
		{
			System.out.print("You failed guessing the secret within MAX_ROUND_NUMBER=" + MAX_ROUND_NUMBER + " rounds");
		}
	}

}// end of class Mastermind

















