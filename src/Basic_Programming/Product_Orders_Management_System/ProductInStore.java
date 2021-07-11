package Basic_Programming.Product_Orders_Management_System;

/**
 * This class represents a product that is in the store ( no shipment )
 * @author Assaf Attias
 *
 */
public class ProductInStore extends Product 
{
	private double shipmentPrice;
	
	/**
	 * Constructor for making a new product.
	 * @param name - the name of the product
	 * @param serialNum - serial number of the product (Unique)
	 * @param price - how much the product is worth
	 */
	public ProductInStore(String name, int serialNum, double price) 
	{
		super(name, serialNum, price);
		shipmentPrice = 0;
	}

	/**
	 * Copy Constructor
	 * @param data - Product to copy from
	 */
	public ProductInStore(Object data) 
	{
		super(data);
		shipmentPrice = 0;
	}

	/**
	 * This function returns the price of the product and the shipment cost for it
	 */
	@Override
	public double[] computeFinalPrice() 
	{
		double[] result = new double[2];
		
		result[0] = getProductPrice();
		result[1] = shipmentPrice;
		
		return result;
	}

}
