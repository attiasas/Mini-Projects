package Basic_Programming.Product_Orders_Management_System;

/**
 * This class represents a VIP client ( has a discount )
 * @author Assaf Attias
 */
public class VIPClient extends Client
{

	// Constructors
	/**
	 * Constructor for client
	 * @param firstName - the first name of the client
	 * @param lastName - the family name of the client
	 * @param id - identification number of the client
	 */
	public VIPClient(String firstName, String lastName, int id) 
	{
		super(firstName, lastName, id);
	}
	
	/**
	 * Copy Constructor
	 * @param data
	 */
	public VIPClient(Object data) 
	{
		super(data);
	}


	// Methods
	
	/**
	 * This function calculate the sum of the shipment price the client will need to pay for the products (with 50% discount)
	 * @return - sum of the shipment price
	 */
	@Override
	public double computeFinalShippingPrice()
	{
		double totalPrice = super.computeFinalShippingPrice();
		
		totalPrice = totalPrice / 2;
		
		return totalPrice;
	}
	
}
