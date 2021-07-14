package Databases.Users_MediaItems_NoSQL.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.bgu.ise.ddb.items.ItemsController;
import org.bgu.ise.ddb.registration.RegistarationController;

import redis.clients.jedis.Jedis;

public class Utils {

	public static final String DB_IP = "XXX.XX.XX.XX";
	
	public static final String USERS_SET = "users";
	public static final String USERS_REG_STAMP = USERS_SET + "_reg_stamp";
	public static final String SEPERATOR = "#";
	
	public static final String MEDIA_ITEM = "mediaItems";
	public static final String MEDIA_ITEM_REG_STAMP = MEDIA_ITEM + "_reg_stamp";
	
	public static final String HISTORY = "history";
	public static final String HISTORY_BY_USER = HISTORY + "_by_users";
	public static final String HISTORY_BY_ITEM = HISTORY + "_by_items";
	
	public static final String ORACLE_URL = "jdbc:oracle:URL";
	public static final String ORACLE_USER = "username";
	public static final String ORACLE_PASS = "password";
	
	public static String getKey(String title, String username)
	{
		return HISTORY + SEPERATOR + title + SEPERATOR  + username;
	}
	
    public static long subtractDays(int days) {
        Calendar calendar = Calendar.getInstance();
        //calendar.set(Calendar.MINUTE,0);
        //calendar.set(Calendar.HOUR,0);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(calendar.getTime());
        cal.add(Calendar.DATE, -days);
       
        return cal.getTimeInMillis();
    }
    
    public static void printUsers() 
    {
    	String s = "== Users Table =====================\n";
    	s += "Format = [ <userName> , <password> , <firstName> , <lastName> ]\n";
    	s += "------------------------------------\n";
    	try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
    		Set<String> users = jedis.smembers(Utils.USERS_SET);
    		int i = 1;
    		for(String username : users)
			{
				String password = jedis.hget(Utils.USERS_SET + Utils.SEPERATOR + username, "password");
				String firstName = jedis.hget(Utils.USERS_SET + Utils.SEPERATOR + username, "firstName");
				String lastName = jedis.hget(Utils.USERS_SET + Utils.SEPERATOR + username, "lastName");
				
				s += i + ". [ " + username + " , " + password + " , " + firstName + " , " + lastName + " ]\n";
				i++;
			}
		}
		catch (Exception e) { e.printStackTrace(); }
    	s += "====================================";
    	System.out.println(s);
    }
    
    public static void printItems() 
    {
    	String s = "== Items Table =====================\n";
    	s += "Format = [ <title> , <prodYear> ]\n";
    	s += "------------------------------------\n";
    	try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
    		Set<String> items = jedis.smembers(Utils.MEDIA_ITEM);
    		int i = 1;
    		for(String title : items)
			{
				String prod_year = jedis.hget(Utils.MEDIA_ITEM + Utils.SEPERATOR + title, "prod_year");
				
				s += i + ". [ " + title + " , " + prod_year + " ]\n";
				i++;
			}
		}
		catch (Exception e) { e.printStackTrace(); }
    	s += "====================================";
    	System.out.println(s);
    }
    
    public static void printHistoryOrderByUsers()
    {
    	String s = "== History Table (Users order) =====\n";
    	s += "Format = [ <userName> ,  <title> , <timeStamp> ]\n";
    	s += "------------------------------------\n";
    	try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
    		Set<String> users = jedis.smembers(Utils.USERS_SET);
    		int i = 1;
    		for(String username : users)
			{
    			Set<String> titles = jedis.smembers(Utils.HISTORY_BY_USER + Utils.SEPERATOR + username);
    			for(String title : titles)
    			{
    				String key = Utils.getKey(title, username);
    				Date timeStamp = new Date(Long.parseLong(jedis.hget(key, "time_stamp")));
    				
    				s += i + ". [ " + username + " , " + title + " , " + timeStamp + " ]\n";
    				i++;
    			}	
			}
		}
		catch (Exception e) { e.printStackTrace(); }
    	s += "====================================";
    	System.out.println(s);
    }
    
    public static void printHistoryOrderByItems()
    {
    	String s = "== History Table (Items order) =====\n";
    	s += "Format = [ <userName> ,  <title> , <timeStamp> ]\n";
    	s += "------------------------------------\n";
    	try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
    		Set<String> titles = jedis.smembers(Utils.MEDIA_ITEM);
    		int i = 1;
    		for(String title : titles)
			{
    			Set<String> usernames = jedis.smembers(Utils.HISTORY_BY_ITEM + Utils.SEPERATOR + title);
    			for(String username : usernames)
    			{
    				String key = Utils.getKey(title, username);
    				Date timeStamp = new Date(Long.parseLong(jedis.hget(key, "time_stamp")));
    				
    				s += i + ". [ " + username + " , " + title + " , " + timeStamp + " ]\n";
    				i++;
    			}	
			}
		}
		catch (Exception e) { e.printStackTrace(); }
    	s += "====================================";
    	System.out.println(s);
    }
    
    public static void printDB()
    {
    	Utils.printUsers();
    	Utils.printItems();
    	Utils.printHistoryOrderByUsers();
    	//Utils.printHistoryOrderByItems();
    }
    
    public static boolean deleteAllHistory()
    {
    	try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
    		List<String> keys = new ArrayList<>();
    		Set<String> titles = jedis.smembers(Utils.MEDIA_ITEM);
        	
        	for(String title : titles)
    		{
        		// delete index by item
        		keys.add(Utils.HISTORY_BY_ITEM + Utils.SEPERATOR + title);
        		
    			Set<String> usernames = jedis.smembers(Utils.HISTORY_BY_ITEM + Utils.SEPERATOR + title);
    			for(String username : usernames)
    			{
    				String key = Utils.getKey(title, username);
    				// delete index by user
    				keys.add(Utils.HISTORY_BY_USER + Utils.SEPERATOR + username);
    				// delete table row
    				keys.add(key);
    			}
    		}
        	
        	if(!keys.isEmpty()) 
        	{
        		String[] keysToDelete = new String[keys.size()];
            	keys.toArray(keysToDelete);
            	jedis.del(keysToDelete);
        	}
		}
		catch (Exception e) 
    	{ 
			e.printStackTrace(); 
			return false;
		}

    	return true;
    }
   
    public static boolean deleteItemsTable()
    {
    	if(!Utils.deleteAllHistory()) return false;
    	
    	try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
    		List<String> keys = new ArrayList<>();
    		
    		Set<String> items = jedis.smembers(Utils.MEDIA_ITEM);
    			
   			for(String title : items)
    		{
   				if(title.equals("Se7en"))
   				{
   					keys.add(Utils.MEDIA_ITEM + Utils.SEPERATOR + title);
   				}
    		}
    			
   			if(!items.isEmpty()) keys.add(Utils.MEDIA_ITEM);
   			
   			if(!keys.isEmpty()) 
        	{
        		String[] keysToDelete = new String[keys.size()];
            	keys.toArray(keysToDelete);
            	jedis.del(keysToDelete);
        	}
		}
    	catch (Exception e) 
    	{ 
			e.printStackTrace(); 
			return false;
		}
    	
    	return true;
    }
    
    public static boolean deleteUsersTable()
    {
    	if(!Utils.deleteAllHistory()) return false;
    	
    	try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
    		List<String> keys = new ArrayList<>();
    		
    		Set<String> users = jedis.smembers(Utils.USERS_SET);
			
    		for(String username : users)
    		{
    			keys.add(Utils.USERS_SET + Utils.SEPERATOR + username);
    		}
    		
    		if(!users.isEmpty()) keys.add(Utils.USERS_SET);
    		
    		if(!keys.isEmpty()) 
        	{
        		String[] keysToDelete = new String[keys.size()];
            	keys.toArray(keysToDelete);
            	jedis.del(keysToDelete);
        	}
		}
    	catch (Exception e) 
    	{ 
			e.printStackTrace(); 
			return false;
		}
    	
    	return true;
    }
    
    public static boolean cleanDB()
    {
    	if(!Utils.deleteItemsTable()) return false;
    	if(!Utils.deleteUsersTable()) return false;
    	return true;
    }
   
	public static void main(String[] args) throws IOException
	{
		// Print Methods
		Utils.printDB();
		
//		RegistarationController r = new RegistarationController();
//		System.out.println(r.getNumberOfRegistredUsers(5));

		/*
		Utils.printUsers();
		Utils.printItems();
		Utils.printHistoryOrderByUsers();
		Utils.printHistoryOrderByItems();
		*/
		//Utils.deleteItemsTable();
		// Delete Tables Methods
		/*
		Utils.cleanDB();
		Utils.deleteAllHistory();
		Utils.deleteItemsTable();
		Utils.deleteUsersTable();
		 */
		//Utils.printDB();
	}
}
