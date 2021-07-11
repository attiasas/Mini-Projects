package Basic_Programming.Product_Orders_Management_System;

/**
 * This class represent a product for sale, each product has different serial number.
 * @author Assaf Attias
 */
public abstract class Product 
{

	// Fields
	private String name;
	private int serialNum;
	private double price;
	
	
	// Constructors
	/**
	 * Constructor for making a new product.
	 * @param name - the name of the product
	 * @param serialNum - serial number of the product (Unique)
	 * @param price - how much the product is worth
	 */
	public Product(String name, int serialNum, double price)
	{
		// Handle Exception
		if(name == null || serialNum <= 0 || price <= 0 || name.length() == 0)
		{
			throw new IllegalArgumentException();
		}
		
		this.name = name;
		this.serialNum = serialNum;
		this.price = price;
	}
	
	/**
	 * Copy Constructor
	 * @param data - Product to copy from
	 */
	public Product(Object data) 
	{
		// Handle Exception
		if(!(data instanceof Product))
		{
			throw new IllegalArgumentException();
		}
		
		Product castData = (Product) data;
		
		this.name = castData.name;
		this.serialNum = castData.serialNum;
		this.price = castData.price;
		
	}


	// Methods
	
	/**
	 * This function returns the price of the product and the shipment cost for it (abstract)
	 * @return array of 2 doubles. 
	 * 		the first is the price for the product.
	 * 		the second is the price for the shipment
	 */
	public abstract double[] computeFinalPrice();
	
	/**
	 * This function returns the name of the product
	 * @return - the name of the product
	 */
	public String getProductName() {return new String(name);}
	
	/**
	 * This function returns the serial number of the product
	 * @return - the serial number of the product
	 */
	public int getProductSerialNumber() {return serialNum;}
	
	/**
	 * This function returns the price of the product
	 * @return - the price of the product
	 */
	public double getProductPrice() {return price;}
	
	/**
	 * This function returns a represantion of the product in the format - "Product: name, serialNum, price"
	 */
	@Override
	public String toString()
	{
		return "Product: " + name + ", " + serialNum + ", " + price;
	}
	
	/**
	 * This function check if a given object is a product and if it has the same serial number
	 */
	@Override
	public boolean equals(Object other)
	{
		boolean isEqual = false;
		
		if((other instanceof Product))
		{
			if(serialNum == ((Product)other).serialNum)
			{
				isEqual = true;
			}
		}
		
		return isEqual;
	}
	
}




