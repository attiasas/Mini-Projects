package Basic_Programming.Product_Orders_Management_System;

/**
 * This class compare two client by the sum of the products they wants
 * @author Assaf Attias
 *
 */
public class ClientTotalProductsPriceComparator implements Comparator 
{
	/**
	 * This function compare clients by the sum of the products they wants
	 * @return
	 * 		1 - if o1 is less than o2
	 * 	   -1 - if o1 is greater than o2
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
		if(((Client)o1).computeFinalProductsPrice() > ((Client)o2).computeFinalProductsPrice())
		{
			result = -1;
		}
		else if(((Client)o1).computeFinalProductsPrice() < ((Client)o2).computeFinalProductsPrice())
		{
			result = 1;
		}

		return result;
	}

}
