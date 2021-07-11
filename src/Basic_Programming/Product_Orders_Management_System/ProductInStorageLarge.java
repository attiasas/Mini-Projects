package Basic_Programming.Product_Orders_Management_System;

/**
 * This class represents a product that is in storage and his size is large ( 10 % shipment )
 * @author Assaf Attias
 *
 */
public class ProductInStorageLarge extends Product 
{
	private double shipmentPrice;
	
	/**
	 * Constructor for making a new product.
	 * @param name - the name of the product
	 * @param serialNum - serial number of the product (Unique)
	 * @param price - how much the product is worth
	 */
	public ProductInStorageLarge(String name, int serialNum, double price) 
	{
		super(name, serialNum, price);
		double precent = 10.0 / 100;
		shipmentPrice = Math.floor(price * precent);
	}

	/**
	 * Copy Constructor
	 * @param data - Product to copy from
	 */
	public ProductInStorageLarge(Object data) 
	{
		super(data);
		double precent = 10.0 / 100;
		shipmentPrice = Math.floor(((Product)data).getProductPrice() * precent);
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
