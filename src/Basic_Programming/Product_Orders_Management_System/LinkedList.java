package Basic_Programming.Product_Orders_Management_System;

/**
 * This class represent a Linked List of objects 
 * @author Assaf Attias
 *
 */
public class LinkedList implements List
{
	// Fields
	private Link head;
	private Link tail;
	private int size;
	
	/**
	 * Constructor, creates an empty linkedList
	 */
	public LinkedList ()
	{
		head = null;
		tail = null;
		size = 0;
	}

	/**
	 * Copy Constructor.
	 * @param list - LinkedList to copy from
	 */
	public LinkedList (LinkedList list)
	{
		if(!list.isEmpty())
		{
			this.head = new Link(list.head);
			Link pointer = this.head;
			while(pointer.getNext() != null)
			{
				Link copyLink = new Link(pointer.getNext());
				pointer.setNext(copyLink);
				pointer = pointer.getNext();
			}
			this.tail = pointer;
			this.size = list.size;
		}
		else
		{
			head = null;
			tail = null;
			size = 0;
		}
	}
	
	
	// Methods
	/**
	 * Adds a new Link to the end of the LinkedList
	 * @param element - an object to add
	 */
	public void add(Object element)
	{
		// Handle Exceptions
		if(element == null)
		{
			throw new NullPointerException();
		}
		
		if(isEmpty())
        {
			Link newLink = new Link(element, null);
			head = newLink;
			tail = newLink;
        }
        else
        {
            Link newLink = new Link(element, null);
            tail.setNext(newLink);
            tail = newLink;
        }
		
		size++;
	}
	
	/**
	 * Adds a new Link to the linkedList in the given index
	 * @param index - index in the linkedList to place the new link
	 * @param element - object to place in the new link.
	 */
	public void add(int index, Object element)
	{
		// Handle Exceptions
		if(index<0 || index>=size)
		{
			throw new IndexOutOfBoundsException();
		}
		else if(element == null)
		{
			throw new NullPointerException();
		}
		
		// Add New Link
		if(index == 0)
		{
	        Link newLink = new Link(element, null);
	        newLink.setNext(head);
	        head = newLink;
		}
		else
		{
			int listIndex = 0;
			Link curr = head;
			while(listIndex + 1 < index)
			{
				curr = curr.getNext();
				listIndex++;
			}
	        Link newLink = new Link(element, null);
	        newLink.setNext(curr.getNext());
	        curr.setNext(newLink);
		}
		
		size++;
	}
	
	/**
	 * This function returns the number of Objects that are stored in the list
	 */
	@Override
	public int size() 
	{
		return size;
	}
	
	/**
	 * This function checks if the list contains the given object
	 */
	@Override
	public boolean contains(Object element) 
	{
		boolean found = false;
		// Handle Exceptions
		if(element != null)
		{
			for(int i = 0; i < size && !found; i++)
			{
				if(get(i).equals(element))
				{
					found = true;
				}
			}
		}
		
		return found;
	}
	
	/**
	 * returns true if the list is empty
	 */
	@Override
	public boolean isEmpty() 
	{
		if(size == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * this function returns the object inside the link
	 */
	@Override
	public Object get(int index) 
	{
		// Handle Exceptions
		if(index < 0 || index >= size)
		{
			throw new IndexOutOfBoundsException();
		}
		
		// Get Object
		Link currentLink = head;
		int currentIndex = 0;
		while(currentIndex != index)
		{
			currentLink = currentLink.getNext();
			currentIndex++;
		}
		return currentLink.getData();
	}
	
	/**
	 * This function replace the object in a given index with a new given object
	 * @param index - index of a link in the list to replace.
	 * @param element - Object to replace
	 * @return - the object that was removed from the list.
	 */
	@Override
	public Object set(int index, Object element) 
	{
		// Handle Exceptions
		if(index < 0 || index >= size)
		{
			throw new IndexOutOfBoundsException();
		}
		else if(element == null)
		{
			throw new NullPointerException();
		}
		
		// Get Link and set
		Link currentLink = head;
		int currentIndex = 0;
		while(currentIndex != index)
		{
			currentLink = currentLink.getNext();
			currentIndex += 1;
		}
		return currentLink.setData(element);

	}
	
	/**
	 * This function sort the list base on a given comparator class
	 * @param comp - Comparator object
	 */
	public void sortBy(Comparator comp)
	{
		// Handle Exception
		if(comp == null)
		{
			throw new NullPointerException();
		}
		
		if(!isEmpty())
		{
			for(int i = 0; i < size - 1; i++)
			{
				int minValueIndex = i;
				
				for(int j = i + 1; j < size; j++)
				{
					if(comp.compare(get(j), get(minValueIndex)) < 0)
					{
						minValueIndex = j;
					}
				}
				
				//Swap
				if(minValueIndex != i)
				{
					Object holder = set(minValueIndex, get(i));
					set(i,holder);
				}
				
			}
			
		}
		
	}
	
	/**
	 * This function returns a representation of the list (and all the objects in it).
	 */
	@Override
	public String toString()
	{
		String result = "";
		
		if(!isEmpty())
		{
			// Get Objects
			for(int i = 0; i < size; i++)
			{
				if(i == size - 1)
				{
					result = result + get(i).toString();
				}
				else
				{
					result = result + get(i).toString() + "\n";
				}
			}
		}
		
		return result;
	}
	
	/**
	 * This function check if an object is a linkedList and all the objects it holds are the same (in the same order)
	 */
	@Override
	public boolean equals(Object other)
	{
		boolean isEqual = false;
		// check object
		if((other instanceof LinkedList) && ((LinkedList)other).size == size)
		{
			isEqual = true;
		}
		
		if(isEqual)
		{
			for(int i = 0; i < size && isEqual; i++)
			{
				if(!get(i).equals(((LinkedList)other).get(i)))
				{
					isEqual = false;
				}
			}
		}
		
		return isEqual;
	}
	
}
