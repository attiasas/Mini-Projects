package Databases.Users_MediaItems_SQL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Assigment {

	private String connectionURL;
	private String username;
	private String password;
	
	private final int TITLE_INDEX = 0;
	private final int PROD_YEAR_INDEX = 1;
	
	private final String INSERT_ITEM = "insert into MediaItems(TITLE,PROD_YEAR) values(?,?)";
	private final String ALL_MID_QUERY = "select MID from MediaItems";
	private final String MID_PAIRS_QUERY = "select MediaItems.TITLE, t.SIMILARITY from \n" +
										   "(select MID2 as MID, SIMILARITY from Similarity where MID1 = ? and SIMILARITY >= 0.3\n" +
										   "union\n" +
										   "select MID1 as MID, SIMILARITY from Similarity where MID2 = ? and SIMILARITY >= 0.3) t\n" +
										   "inner join MediaItems on MediaItems.MID = t.MID order by t.SIMILARITY asc";

	public Assigment(String connectionURL, String username, String password)
	{
		this.connectionURL = connectionURL;
		this.username = username;
		this.password = password;
	}
	
	public void fileToDataBase(String csv_file_path)
	{
		try 
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			try(BufferedReader br = new BufferedReader(new FileReader(csv_file_path));
				Connection connection = DriverManager.getConnection(connectionURL,username,password);
				PreparedStatement ps = connection.prepareStatement(INSERT_ITEM))
			{
				String line;
					
				while((line = br.readLine()) != null)
				{
					String[] data = line.split(",");
					if(data == null || data.length == 0) continue;
					if(data.length != 2)
						throw new Exception("Bad Data Fornat, Should have 2 columns (Title,PROD_YEAR), found " + data.length + " columns");
						
					ps.setString(1, data[TITLE_INDEX]);
					ps.setInt(2,Integer.parseInt(data[PROD_YEAR_INDEX]));
					
					ps.executeUpdate();
				}
				
			}
			catch(Exception e) { e.printStackTrace(); }
		} catch (ClassNotFoundException e1) { e1.printStackTrace(); }
	}

    public boolean calculateSimilarity()
    {
        Connection conn = null;
        PreparedStatement psInsertUpdate = null;
        boolean succeeded = false;
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(this.connectionURL, this.username, this.password);
            String insertQuery = "insert into MediaItems(title, prod_year) values (?,?)";
            String updateQuery = "update MediaItems set similarity = ? where (mid1 = ? and mid2 = ?) or (mid1=? amd mid2=?)";

            List<Integer> mids = new ArrayList<>();
            ResultSet rsMids = conn.prepareStatement("select mid from mediaitems order by mid").executeQuery();


            while(rsMids.next())
            {//get all mids from MediaItems table
                mids.add(rsMids.getInt(1));
            }

            for(int i : mids)
            {
                for(int j : mids)
                {
                    if(i>j)
                    {
                        continue;
                    }
                    String mergeQuery = "merge into similarity org using (select ? as mid1, ? as mid2, simCalculation(?,?,MaximalDistance()) as similarity from dual) toInsert ON( (toInsert.mid1 = org.mid1 and toInsert.mid2=org.mid2) or toInsert.mid1=org.mid2 and toInsert.mid2=org.mid1)\n" +
                            "when matched then \n" +
                            "update set org.similarity = simCalculation(?,?,MaximalDistance())\n" +
                            "when not matched then\n" +
                            "insert (mid1, mid2, similarity) values(?,?,simCalculation(?,?,MaximalDistance()))";
                    psInsertUpdate = conn.prepareStatement(mergeQuery);
                    psInsertUpdate.setInt(1, mids.get(i));
                    psInsertUpdate.setInt(2, mids.get(j));
                    psInsertUpdate.setInt(3, mids.get(i));
                    psInsertUpdate.setInt(4, mids.get(j));
                    psInsertUpdate.setInt(5, mids.get(i));
                    psInsertUpdate.setInt(6, mids.get(j));
                    psInsertUpdate.setInt(7, mids.get(i));
                    psInsertUpdate.setInt(8, mids.get(j));
                    psInsertUpdate.setInt(9, mids.get(i));
                    psInsertUpdate.setInt(10, mids.get(j));
                    psInsertUpdate.executeUpdate();
                }
            }
            succeeded = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try{
                if(psInsertUpdate != null)
                {
                    psInsertUpdate.close();
                }
                if(conn != null)
                {
                    conn.close();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return succeeded;
    }

	private float getDistValueFromCS(CallableStatement statement, int type) throws SQLException {
		statement.registerOutParameter(1, type);
		statement.execute();
		return statement.getFloat(1);
	}
	
	public void printSimilarItems(long mid)
	{
		try 
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			try(Connection connection = DriverManager.getConnection(connectionURL,username,password);
				PreparedStatement ps = connection.prepareStatement(MID_PAIRS_QUERY))
			{
				ps.setLong(1,mid);
				ps.setLong(2,mid);
				ResultSet result = ps.executeQuery();

				while (result.next())
				{
					System.out.println("Title: " + result.getString(1) + " (Similarity: " + result.getFloat(2) + ")");
				}

				result.close();
			}
			catch(Exception e) { e.printStackTrace(); }
		} catch (ClassNotFoundException e1) { e1.printStackTrace(); }
	}

    public static void main(String[] args) {
        String file = "films.csv";
        String url = "jdbc:oracle:url";
        String user = "username";
        String pass = "password";

        Assigment test = new Assigment(url,user,pass);
        test.fileToDataBase(file);
        test.calculateSimilarity();
        test.printSimilarItems(5);

    }
}
