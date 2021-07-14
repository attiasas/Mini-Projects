/**
 * 
 */
package Databases.Users_MediaItems_NoSQL.src.history;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.bgu.ise.ddb.ParentController;
import org.bgu.ise.ddb.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import utils.Utils;

/**
 * @author Assaf
 *
 */
@RestController
@RequestMapping(value = "/history")
public class HistoryController extends ParentController{

	/**
	 * The function inserts to the system storage triple(s)(username, title, timestamp). 
	 * The timestamp - in ms since 1970
	 * Advice: better to insert the history into two structures( tables) in order to extract it fast one with the key - username, another with the key - title
	 * @param username
	 * @param title
	 * @param response
	 */
	@RequestMapping(value = "insert_to_history", method={RequestMethod.GET})
	public void insertToHistory (@RequestParam("username")    String username,
			@RequestParam("title")   String title,
			HttpServletResponse response){
		//System.out.println("history add " + username+" "+title);
		
		long time_stamp = System.currentTimeMillis();
		String key = Utils.getKey(title, username);
		
		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			
			if(jedis.sismember(Utils.USERS_SET, username) && jedis.sismember(Utils.MEDIA_ITEM, title))
			{
				Transaction t = jedis.multi();
				// add data by joint key
				t.hset(key, "username",username);
				t.hset(key, "title", title);
				t.hset(key, "time_stamp", "" + time_stamp);
				// add indexes
				t.sadd(Utils.HISTORY_BY_USER + Utils.SEPERATOR + username, title);
				t.sadd(Utils.HISTORY_BY_ITEM + Utils.SEPERATOR + title, username);
				t.exec();
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		
		HttpStatus status = HttpStatus.OK;
		response.setStatus(status.value());
	}
	
	/**
	 * The function retrieves  users' history
	 * The function return array of pairs <title,viewtime> sorted by VIEWTIME in descending order
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "get_history_by_users",headers="Accept=*/*", method={RequestMethod.GET},produces="application/json")
	@ResponseBody
	@org.codehaus.jackson.map.annotate.JsonView(HistoryPair.class)
	public  HistoryPair[] getHistoryByUser(@RequestParam("entity")    String username){
		//System.out.println("history ByUser "+ username);
		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			Set<String> titles = jedis.smembers(Utils.HISTORY_BY_USER + Utils.SEPERATOR + username);
			
			HistoryPair[] result = new HistoryPair[titles.size()];
			List<HistoryPair> history = new ArrayList<>(titles.size());
			
			for(String title : titles)
			{
				String key = Utils.getKey(title, username);
				Date timeStamp = new Date(Long.parseLong(jedis.hget(key, "time_stamp")));
				
				history.add(new HistoryPair(title, timeStamp));
			}
			
			history.sort(Comparator.comparing(HistoryPair::getViewtime).reversed());
			history.toArray(result);
			
			return result;
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return new HistoryPair[]{};
	}
	
	
	/**
	 * The function retrieves  items' history
	 * The function return array of pairs <username,viewtime> sorted by VIEWTIME in descending order
	 * @param title
	 * @return
	 */
	@RequestMapping(value = "get_history_by_items",headers="Accept=*/*", method={RequestMethod.GET},produces="application/json")
	@ResponseBody
	@org.codehaus.jackson.map.annotate.JsonView(HistoryPair.class)
	public  HistoryPair[] getHistoryByItems(@RequestParam("entity")    String title){
		
		//System.out.println("history ByItem " + title);
		
		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			Set<String> usernames = jedis.smembers(Utils.HISTORY_BY_ITEM + Utils.SEPERATOR + title);
			
			HistoryPair[] result = new HistoryPair[usernames.size()];
			List<HistoryPair> history = new ArrayList<>(usernames.size());
			
			for(String username : usernames)
			{
				String key = Utils.getKey(title, username);
				Date timeStamp = new Date(Long.parseLong(jedis.hget(key, "time_stamp")));
				
				history.add(new HistoryPair(username, timeStamp));
			}
			
			history.sort(Comparator.comparing(HistoryPair::getViewtime).reversed());
			history.toArray(result);
			
			return result;
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return new HistoryPair[]{};
	}
	
	/**
	 * The function retrieves all the  users that have viewed the given item
	 * @param title
	 * @return
	 */
	@RequestMapping(value = "get_users_by_item",headers="Accept=*/*", method={RequestMethod.GET},produces="application/json")
	@ResponseBody
	@org.codehaus.jackson.map.annotate.JsonView(HistoryPair.class)
	public  User[] getUsersByItem(@RequestParam("title") String title){
		
		//System.out.println("users by title: " + title);
		
		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			Set<String> usernames = jedis.smembers(Utils.HISTORY_BY_ITEM + Utils.SEPERATOR + title);
			User[] result = new User[usernames.size()];
			
			int i = 0;
			for(String username : usernames)
			{
				String firstName = jedis.hget(Utils.USERS_SET + Utils.SEPERATOR + username, "firstName");
				String lastName = jedis.hget(Utils.USERS_SET + Utils.SEPERATOR + username, "lastName");
				
				result[i] = new User(username, firstName, lastName);
				i++;
			}
			
			return result;
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return new User[]{};
	}
	
	/**
	 * The function calculates the similarity score using Jaccard similarity function:
	 *  sim(i,j) = |U(i) intersection U(j)|/|U(i) union U(j)|,
	 *  where U(i) is the set of usernames which exist in the history of the item i.
	 * @param title1
	 * @param title2
	 * @return
	 */
	@RequestMapping(value = "get_items_similarity",headers="Accept=*/*", method={RequestMethod.GET},produces="application/json")
	@ResponseBody
	public double  getItemsSimilarity(@RequestParam("title1") String title1,
			@RequestParam("title2") String title2){
		//System.out.println("sim of titles" + title1 + "," + title2);
		double intersection = 0.0;
		int union = 0;
		
		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			Set<String> usernames_title1 = jedis.smembers(Utils.HISTORY_BY_ITEM + Utils.SEPERATOR + title1);
			Set<String> usernames_title2 = jedis.smembers(Utils.HISTORY_BY_ITEM + Utils.SEPERATOR + title2);
			union = usernames_title2.size();
			
			if(union == 0 && usernames_title1.size() == 0) return 0.0;
			
			for(String username_title1 : usernames_title1)
			{
				if(usernames_title2.contains(username_title1))
				{
					intersection++;
				}
				else
				{
					union++;
				}
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return intersection / union;
	}
	

}
