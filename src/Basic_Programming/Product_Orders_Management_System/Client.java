package Basic_Programming.Product_Orders_Management_System;

/**
 * This class represents a client
 * @author Assaf Attias
 */
public class Client 
{
	// Fields
	private String firstName;
	private String lastName;
	private int id;
	private LinkedList products;
	
	
	// Constructors
	/**
	 * Constructor for client
	 * @param firstName - the first name of the client
	 * @param lastName - the family name of the client
	 * @param id - identification number of the client
	 */
	public Client(String firstName, String lastName, int id)
	{
		// Handle Exception
		if(firstName == null || lastName == null || id <= 0 || firstName.length() == 0 || lastName.length() == 0)
		{
			throw new IllegalArgumentException();
		}
		
		this.firstName =  new String(firstName);
		this.lastName =  new String(lastName);
		this.id = id;
		
		products = new LinkedList();
	}

	/**
	 * Copy Constructor
	 * @param data - Client to copy
	 */
	public Client(Object data) 
	{	
		// Handle Exception
		if(!(data instanceof Client))
		{
			throw new IllegalArgumentException();
		}
		
		Client holder = (Client) data;
		
		firstName = new String(holder.firstName);
		lastName = new String(holder.lastName);
		id = holder.id;
		
		products = new LinkedList(holder.products);
	}

	// Methods
	/**
	 * This function returns the last name of the client
	 * @return - last name of the client
	 */
	public String getLastName() {return new String(lastName);}
	
	/**
	 * This function returns the first name of the client
	 * @return - first name of the client
	 */
	public String getFirstName() {return new String(firstName);}
	
	/**
	 * This function returns the id of the client
	 * @return - id of the client
	 */
	public int getId() {return id;}
	
	/**
	 * This function returns the list of products the client wants to order
	 * @return - the products the client wants to order
	 */
	public LinkedList getProducts() 
	{
		LinkedList copyList = new LinkedList(products);
		return copyList;
	}
	
	/**
	 * This function check if a given product is in the list of products the client wants 
	 * @param product - an product to check
	 * @return - true if the product is in the list (check by ID)
	 */
	public boolean isInterestedIn(Product product)
	{
		return products.contains(product);
	}
	
	/**
	 * This function adds a product to the client product list if the product is not on the list already
	 * @param product - a product to add
	 * @return - true if the product was added
	 */
	public boolean addProduct(Product product)
	{
		boolean added = false;
		
		if(!isInterestedIn(product))
		{
			products.add(product);
			added = true;
		}
		
		return added;
	}

	/**
	 * This function returns the representation of the client and the products he/she wants
	 */
	@Override
	public String toString()
	{
		return "Client: " + firstName + " " + lastName + ", " + id + ", \n" + products;
	}
	
	/**
	 * this function check if a given object is a client and if it has the same id
	 */
	@Override
	public boolean equals(Object other)
	{
		boolean isEqual = false;
		
		if((other instanceof Client))
		{
			if(id == ((Client)other).id)
			{
				isEqual = true;
			}
		}
		
		return isEqual;
	}
	
	/**
	 * This function calculate the sum of the products the client wants base on their price
	 * @return - sum of the products
	 */
	public double computeFinalProductsPrice() 
	{
		double sumOfProducts = 0;
		
		for(int i = 0; i < products.size(); i++)
		{
			sumOfProducts += ((Product)products.get(i)).getProductPrice();
		}
		
		return sumOfProducts;
	}
	
	/**
	 * This function calculate the sum of the shipment price the client will need to pay for the products
	 * @return - sum of the shipment price
	 */
	public double computeFinalShippingPrice()
	{
		double sum = 0;
		
		for(int i = 0; i < products.size(); i++)
		{
			double[] prices = ((Product)(products.get(i))).computeFinalPrice();
			sum += prices[1];
		}
		
		return sum;
	}
	
	/**
	 * This function calculate the final price for the products (price for the products and shipment)
	 * @return - final price the client needs to pay
	 */
	public double computeFinalOrderPrice()
	{
		return computeFinalProductsPrice() + computeFinalShippingPrice();
	}
	
	
}



