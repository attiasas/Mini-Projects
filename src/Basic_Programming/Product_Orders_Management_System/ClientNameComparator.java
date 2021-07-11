package Basic_Programming.Product_Orders_Management_System;

/**
 * This class compare two client by the last name (and first name if necessary)
 * @author Assaf Attias
 *
 */
public class ClientNameComparator implements Comparator
{
	/**
	 * This function compare clients by their last name (and first name if the last name is the same)
	 * @return
	 * 		1 - if o1 is greater than o2
	 * 	   -1 - if o1 is less than o2
	 */
	@Override
	public int compare(Object o1, Object o2) 
	{
		int result = 0;
		
		// Handle Exceptions
		if(!(o1 instanceof Client) || !(o2 instanceof Client))
		{
			throw new ClassCastException();
		}
		
		// Compare
		if(((Client)o1).getLastName().compareTo(((Client)o2).getLastName()) > 0)
		{
			result = 1;
		}
		else if(((Client)o1).getLastName().compareTo(((Client)o2).getLastName()) < 0)
		{
			result = -1;
		}
		else
		{
			if(((Client)o1).getFirstName().compareTo(((Client)o2).getFirstName()) > 0)
			{
				result = 1;
			}
			else if(((Client)o1).getFirstName().compareTo(((Client)o2).getFirstName()) < 0)
			{
				result = -1;
			}
		}
		
		return result;
	}

}
