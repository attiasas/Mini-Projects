/**
 * 
 */
package Databases.Users_MediaItems_NoSQL.src.registration;



import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
@RequestMapping(value = "/registration")
public class RegistarationController extends ParentController{
	
	
	/**
	 * The function checks if the username exist,
	 * in case of positive answer HttpStatus in HttpServletResponse should be set to HttpStatus.CONFLICT,
	 * else insert the user to the system  and set to HttpStatus in HttpServletResponse HttpStatus.OK
	 * @param username
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param response
	 */
	@RequestMapping(value = "register_new_customer", method={RequestMethod.POST})
	public void registerNewUser(@RequestParam("username") String username,
			@RequestParam("password")    String password,
			@RequestParam("firstName")   String firstName,
			@RequestParam("lastName")  String lastName,
			HttpServletResponse response){
		
		//System.out.println(username+" "+password+" "+lastName+" "+firstName);
		
		HttpStatus status = HttpStatus.CONFLICT;
		
		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			if(!isExistUser(username))
			{
				Transaction t = jedis.multi();
				t.hset(Utils.USERS_SET + Utils.SEPERATOR + username, "password", password);
				t.hset(Utils.USERS_SET + Utils.SEPERATOR + username, "lastName", lastName);
				t.hset(Utils.USERS_SET + Utils.SEPERATOR + username, "firstName", firstName);
				
				t.sadd(Utils.USERS_SET, username);
				
				t.zadd(Utils.USERS_REG_STAMP, System.currentTimeMillis(), username);
				t.exec();
				
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		
		response.setStatus(status.value());
	}
	
	/**
	 * The function returns true if the received username exist in the system otherwise false
	 * @param username
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "is_exist_user", method={RequestMethod.GET})
	public boolean isExistUser(@RequestParam("username") String username) throws IOException{
		
		boolean result = false;
		
		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			result = jedis.sismember(Utils.USERS_SET, username);
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return result;
		
	}
	
	/**
	 * The function returns true if the received username and password match a system storage entry, otherwise false
	 * @param username
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "validate_user", method={RequestMethod.POST})
	public boolean validateUser(@RequestParam("username") String username,
			@RequestParam("password")    String password) throws IOException{
		//System.out.println(username+" "+password);
		boolean result = false;
		
		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			if(isExistUser(username))
			{
				String db_pass = jedis.hget(Utils.USERS_SET + Utils.SEPERATOR + username, "password");
				result = db_pass.equals(password);
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return result;
		
	}
	
	/**
	 * The function retrieves number of the registered users in the past n days
	 * @param days
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "get_number_of_registred_users", method={RequestMethod.GET})
	public int getNumberOfRegistredUsers(@RequestParam("days") int days) throws IOException{
		
		int result = 0;

		long time_before_days = Utils.subtractDays(days);
		
		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			Set<String> users_reg = jedis.zrangeByScore(Utils.USERS_REG_STAMP, time_before_days, Double.POSITIVE_INFINITY);
			result = users_reg.size();
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return result;
		
	}
	
	/**
	 * The function retrieves all the users
	 * @return
	 */
	@RequestMapping(value = "get_all_users",headers="Accept=*/*", method={RequestMethod.GET},produces="application/json")
	@ResponseBody
	@org.codehaus.jackson.map.annotate.JsonView(User.class)
	public  User[] getAllUsers(){

		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			Set<String> users = jedis.smembers(Utils.USERS_SET);
			User[] res = new User[users.size()];
			
			int i = 0;
			for(String username : users)
			{
				String password = jedis.hget(Utils.USERS_SET + Utils.SEPERATOR + username, "password");
				String firstName = jedis.hget(Utils.USERS_SET + Utils.SEPERATOR + username, "firstName");
				String lastName = jedis.hget(Utils.USERS_SET + Utils.SEPERATOR + username, "lastName");
				
				res[i] = new User(username, password, firstName, lastName);
				i++;
			}
			
			return res;
			
		}
		catch (Exception e) { e.printStackTrace(); }

		return new User[]{};
	}

}
