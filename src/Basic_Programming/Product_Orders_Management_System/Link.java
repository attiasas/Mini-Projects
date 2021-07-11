package Basic_Programming.Product_Orders_Management_System;

/**
 * This class represents a Link from LinkedList that can store Objects of Clients/Products
 * @author Assaf Attias
 */
public class Link
{
	// Fields
	private Object data;
	private Link next;
	
	/**
	 * Constructor for one link without connection
	 * @param data - object to store
	 */
	public Link(Object data)
	{
		this(data, null);
	}
	
	/**
	 * Constructor for link with connection
	 * @param data - object to store
	 * @param next - address to the next Link
	 */
	public Link(Object data, Link next)	
	{
		this.data = getCopy(data);
		this.next = next;
	}
	
	/**
	 * Copy Constructor
	 * @param link - link to copy from
	 */
	public Link(Link link)	{
		
		this.data = getCopy(link.getData());
			
		if(link.getNext() != null)
		{
			this.next = new Link(link.getNext());
		}
		else
		{
			this.next = null;
		}
	}

	
	// Methods
	
	/**
	 * This function returns a copy of an object 
	 * @param object - object to copy
	 * @return copy of object
	 */
	private Object getCopy(Object object)
	{
		
		if(object instanceof VIPClient)
		{
			return new VIPClient(object);
		}
		else if(object instanceof Client)
		{
			return new Client(object);
		}
		else if(object instanceof ProductInStore)
		{
			return new ProductInStore(object);
		}
		else if(object instanceof ProductInStorageMedium)
		{
			return new ProductInStorageMedium(object);
		}
		else if(object instanceof ProductInStorageLarge)
		{
			return new ProductInStorageLarge(object);
		}
		else
		{
			return new ProductInStorageSmall(object);
		}

	}
	
	/**
	 * get the data that the link stores
	 * @return - object in link
	 */
	public Object getData()
	{
		return data;
	}
	
	/**
	 * get the address to the next link
	 * @return address to the next link (can be null)
	 */
	public Link getNext() 
	{
		return next;
	}
	
	/**
	 * set the address of the link.
	 * @param next - address
	 */
	public void setNext(Link next) 
	{
		this.next = next;
	}
	
	/**
	 * Replace the object that the link stores with a given one 
	 * @param data - object to store in link
	 * @return - object that was stored and removed from the link
	 */
	public Object setData(Object data) 
	{
		Object res = this.data;
		this.data = getCopy(data);
		return res;
	}
	
	/**
	 * Representation of the object that is stored in the link
	 */
	@Override
	public String toString()
	{
		return data.toString();
	}
	
	/**
	 * Compare an object to this link, two links are equals if they store the same object.
	 */
	@Override
	public boolean equals(Object other) 
	{
		if((other instanceof Link))
		{
			if(((Link)other).data.equals(data))
			{
				return true;
			}
		}
		
		return false;
	}
}
