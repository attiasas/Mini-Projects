package Basic_Programming.Product_Orders_Management_System;

/**
 * This class represents a represents object to manage a list of clients and products the shop have
 * @author Assaf Attias
 *
 */
public class ClientProductManagementSystem 
{
	// Fields
	private LinkedList clients;
	private LinkedList products;
	
	// Constructors
	/**
	 * Default Constructor
	 */
	public ClientProductManagementSystem()
	{
		clients = new LinkedList();
		products = new LinkedList();
	}
	
	// Methods
	
	/**
	 * This class adds a new client to the clients list
	 * @param 
	 * 		client - client to add
	 * @return
	 * 		true - if the list does not contains the client and it was added successfully
	 */
	public boolean addClient(Client client)
	{
		boolean addedClient = false;
		
		if(!clients.contains(client))
		{
			clients.add(client);
			addedClient = true;
		}
		
		return addedClient;
	}
	
	/**
	 * This class adds a new product to the products list
	 * @param 
	 * 		product - client to add
	 * @return
	 * 		true - if the list does not contains the product and it was added successfully
	 */
	public boolean addProduct(Product product)
	{
		boolean addedProduct = false;
		
		if(!products.contains(product))
		{
			products.add(product);
			addedProduct = true;
		}
		
		return addedProduct;
	}
	
	/**
	 * This function adds a product to a client's (from the client list) products.
	 * @param client - client form the client list
	 * @param product - product from the product list
	 * @return
	 */
	public boolean addProductToClient(Client client, Product product)
	{
		boolean legalInput = false;
		boolean result = false;
		int clientIndex = -1;
		int productIndex = -1;
		
		// check if exists
		for(int i = 0; i < clients.size() && !legalInput; i++)
		{
			legalInput = clients.get(i).equals(client);
			if(legalInput)
			{
				clientIndex = i;
			}
		}
		
		if(legalInput)
		{
			legalInput = false;
			for(int i = 0; i < products.size() && !legalInput; i++)
			{
				legalInput = products.get(i).equals(product);
				if(legalInput)
				{
					productIndex = i;
				}
			}
		}
		
		// add to client
		if(legalInput)
		{
			result = ((Client)clients.get(clientIndex)).addProduct(((Product)products.get(productIndex)));
		}
		
		return result;
	}
	
	/**
	 * This function returns a list of the first k base on a given comparator 
	 * @param comp - comparator to compare by
	 * @param k - number of clients in the list
	 * @return - a list of clients
	 */
	public LinkedList getFirstKClients(Comparator comp, int k)
	{
		// Handle Exception
		if(comp == null || k <= 0 || k > getNumberOfClients())
		{
			throw new IllegalArgumentException();
		}
		
		// Get List
		LinkedList resultList = new LinkedList();
		clients.sortBy(comp);
		
		for(int i = 0; i < k; i++)
		{
			resultList.add(clients.get(i));
		}
		resultList.sortBy(comp);
		
		return resultList;
	}
	
	/**
	 * getter for the number of clients the object has
	 * @return - number of clients
	 */
	public int getNumberOfClients() {return clients.size();}
	
	/**
	 * getter for the number of products the object has
	 * @return - number of products
	 */
	public int getNumberOfProducts() {return products.size();}
	
	/**
	 * 
	 * @param client
	 * @return
	 */
	public double computeFinalOrderPrice(Client client)
	{
		double finalPrice = 0;
		boolean legalInput = false;
		// Check if client in the list
		for(int i = 0; i < clients.size() && !legalInput; i++)
		{
			legalInput = clients.get(i).equals(client);
			if(legalInput)
			{
				// Compute
				finalPrice = ((Client)clients.get(i)).computeFinalOrderPrice();
			}
		}
		
		return finalPrice;
	}
	
}



