/**
 * 
 */
package Databases.Users_MediaItems_NoSQL.src.items;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.bgu.ise.ddb.MediaItems;
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
@RequestMapping(value = "/items")
public class ItemsController extends ParentController {
	
	/**
	 * The function copy all the items(title and production year) from the Oracle table MediaItems to the System storage.
	 * The Oracle table and data should be used from the previous assignment
	 */
	@RequestMapping(value = "fill_media_items", method={RequestMethod.GET})
	public void fillMediaItems(HttpServletResponse response){
		
		String query = "SELECT TITLE,PROD_YEAR FROM MediaItems";
		
		Utils.deleteItemsTable(); // clean table
		
		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			try(Connection connection = DriverManager.getConnection(Utils.ORACLE_URL,Utils.ORACLE_USER,Utils.ORACLE_PASS);
				PreparedStatement ps = connection.prepareStatement(query);
				ResultSet query_res = ps.executeQuery())
			{
				Transaction t = jedis.multi();
				while(query_res.next())
				{
					String title = query_res.getString(1);
					int prod_year = query_res.getInt(2);

					t.sadd(Utils.MEDIA_ITEM, title);
					t.hset(Utils.MEDIA_ITEM + Utils.SEPERATOR + title, "prod_year", "" + prod_year);
				}
				t.exec();
			}
			catch(Exception e) { e.printStackTrace(); }	
		}
		catch (Exception e) { e.printStackTrace(); }
		
		HttpStatus status = HttpStatus.OK;
		response.setStatus(status.value());
	}
	
	

	/**
	 * The function copy all the items from the remote file,
	 * the remote file have the same structure as the films file from the previous assignment.
	 * You can assume that the address protocol is http
	 * @throws IOException 
	 */
	@RequestMapping(value = "fill_media_items_from_url", method={RequestMethod.GET})
	public void fillMediaItemsFromUrl(@RequestParam("url")    String urladdress,
			HttpServletResponse response) throws IOException{
		System.out.println(urladdress);
		
		Utils.deleteItemsTable(); // clean table
		
		try(Jedis jedis = new Jedis(Utils.DB_IP);
				Scanner csv_scanner = new Scanner(new URL(urladdress).openStream()))
		{
			int TITLE_INDEX = 0;
			int PROD_YEAR_INDEX = 1;
			
			Transaction t = jedis.multi();
			while(csv_scanner.hasNext())
			{
				try
				{
					String[] data = csv_scanner.next().split(",");
					if(data == null || data.length != 2) continue;
							
					String title = data[TITLE_INDEX];
					int prod_year = Integer.parseInt(data[PROD_YEAR_INDEX]);

					t.sadd(Utils.MEDIA_ITEM, title);
					t.hset(Utils.MEDIA_ITEM + Utils.SEPERATOR + title, "prod_year", "" + prod_year);
				}
				catch(Exception e) { e.printStackTrace(); }
			}
			t.exec();
		}
		catch(Exception e) { e.printStackTrace(); }	
		
		HttpStatus status = HttpStatus.OK;
		response.setStatus(status.value());
	}
	
	
	/**
	 * The function retrieves from the system storage N items,
	 * order is not important( any N items) 
	 * @param topN - how many items to retrieve
	 * @return
	 */
	@RequestMapping(value = "get_topn_items",headers="Accept=*/*", method={RequestMethod.GET},produces="application/json")
	@ResponseBody
	@org.codehaus.jackson.map.annotate.JsonView(MediaItems.class)
	public  MediaItems[] getTopNItems(@RequestParam("topn")    int topN){
		
		try(Jedis jedis = new Jedis(Utils.DB_IP))
		{
			ArrayList<MediaItems> list = new ArrayList<>();
			Iterator<String> items_iterator = jedis.smembers(Utils.MEDIA_ITEM).iterator();
			
			while(list.size() < topN && items_iterator.hasNext())
			{
				try
				{
					String title = items_iterator.next();
					int prod_year = Integer.parseInt(jedis.hget(Utils.MEDIA_ITEM + Utils.SEPERATOR + title, "prod_year"));
					//System.out.println("I = [" + title + "," + prod_year + "]");
					list.add(new MediaItems(title, prod_year));
				}
				catch(Exception e) { e.printStackTrace(); }
			}
			
			MediaItems[] result = new MediaItems[list.size()];
			for(int i = 0; i < result.length; i++)
			{
				result[i] = list.get(i);
			}
			
			return result;
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return new MediaItems[]{};
	}

}
