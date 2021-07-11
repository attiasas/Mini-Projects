package Basic_Programming.Suduko;

/**
 * This class has a variety of recursive functions base on each task that was given
 * @author Assaf Attias
 */
public class Warmup {

	public static void main(String[] args) {
		
	}
	
	// **************   Task1   **************
	/** 
	 * This function check if a given digit is shown in a number. recursively
	 *  (19,1)---> true, (24,3)---> false
	 */
	public static boolean doesDigitAppearInNumber(int number, int digit) {
		if(number == 0 && digit != 0)
		{
			return false;
		}
		else if(number % 10 == digit)
		{
			return true;
		}
		else
		{
			return doesDigitAppearInNumber(number / 10,digit);
		}
	}

	// ************** Task2 **************
	/**
	 * This function Check how many Even digits are in a given number. recursively
	 */
	public static int countNumberOfEvenDigits(int number) {
		if(number == 0)
		{
			return 0;
		}
		else if((number % 10) % 2 == 0)
		{
			return 1 + countNumberOfEvenDigits(number / 10);
		}
		else
		{
			return countNumberOfEvenDigits(number / 10);
		}
	}

	// ************** Task3 **************
	/**
	 * This function counts how many times aCHAR appear in a given String. recursively
	 */
	public static int countTheAmountOfCharInString(String str, char c) {
		if(str.length() == 0)
		{
			return 0;
		}
		else if(str.charAt(0) == c)
		{
			return 1 + countTheAmountOfCharInString(str.substring(1),c);
		}
		else
		{
			return countTheAmountOfCharInString(str.substring(1),c);
		}
	}

	// ************** Task4 **************
	/**
	 * This function check if a given String has only capital letters or only small letters. recursively.
	 */
	public static boolean checkIfAllLettersAreCapitalOrSmall(String str) {
		if(str.equals(""))
		{
			return false;
		}
		else if(str.length() <= 1)
		{
			return true;
		}
		else if((str.charAt(0) -'a' >= 0 && str.charAt(0) - 'a' <= 25 && str.charAt(1) -'a' >= 0 && str.charAt(1) - 'a' <= 25) || (str.charAt(0) -'A' >= 0 && str.charAt(0) - 'A' <= 25 && str.charAt(1) -'A' >= 0 && str.charAt(1) - 'A' <= 25))
		{
			return true && checkIfAllLettersAreCapitalOrSmall(str.substring(1));
		}
		else
		{
			return false;
		}
	}

}
