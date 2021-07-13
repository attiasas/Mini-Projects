package Databases.DB_Application;// Assaf Attias

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;



public class Application {


    private Application() {
    }

   public static void executeFunc(Application ass, String[] args) {
        String funcName = args[0];
        switch (funcName) {
            case "loadNeighborhoodsFromCsv":
                ass.loadNeighborhoodsFromCsv(args[1]);
                break;
            case "dropDB":
                ass.dropDB();
                break;
            case "initDB":
                ass.initDB(args[1]);
                break;
            case "updateEmployeeSalaries":
                ass.updateEmployeeSalaries(Double.parseDouble(args[1]));
                break;
            case "getEmployeeTotalSalary":
                System.out.println(ass.getEmployeeTotalSalary());
                break;
            case "updateAllProjectsBudget":
                ass.updateAllProjectsBudget(Double.parseDouble(args[1]));
                break;
            case "getTotalProjectBudget":
                System.out.println(ass.getTotalProjectBudget());
                break;
            case "calculateIncomeFromParking":
                System.out.println(ass.calculateIncomeFromParking(Integer.parseInt(args[1])));
                break;
            case "getMostProfitableParkingAreas":
                System.out.println(ass.getMostProfitableParkingAreas());
                break;
            case "getNumberOfParkingByArea":
                System.out.println(ass.getNumberOfParkingByArea());
                break;
            case "getNumberOfDistinctCarsByArea":
                System.out.println(ass.getNumberOfDistinctCarsByArea());
                break;
            case "AddEmployee":
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                ass.AddEmployee(Integer.parseInt(args[1]), args[2], args[3], Date.valueOf(args[4]), args[5], Integer.parseInt(args[6]), Integer.parseInt(args[7]), args[8]);
                break;
            default:
                break;
        }
    }



    public static void main(String[] args) {
    	
    	File file = new File(".");
        String csvFile = args[0];
        String line = "";
        String cvsSplitBy = ",";
        Application ass = new Application();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] row = line.split(cvsSplitBy);

                executeFunc(ass, row);

            }

        } catch (IOException e) {
            e.printStackTrace();

        } 
        
    }


    private Connection connectToDB () throws Exception
    {
    	String connectionString = "jdbc:sqlserver://localhost;instance=SQLEXPRESS;integratedSecurity=true;databaseName=DB2019_Ass2";
    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    	Connection conn;
    	conn = DriverManager.getConnection(connectionString);
    	return conn;
    }

    private void loadNeighborhoodsFromCsv(String csvPath) 
    {
    	Connection conn;
    	String line="";
    	try (BufferedReader br = new BufferedReader(new FileReader(csvPath)))
    	{
    		conn = connectToDB();
    		PreparedStatement ps = conn.prepareStatement("insert into neighborhood(NID, Name) values (?, ?)");
            while ((line = br.readLine()) != null)
            {
                // use comma as separator
                String[] row = line.split(",");
                ps.setInt(1, Integer.parseInt(row[0]));
                ps.setString(2, row[1]);
                ps.executeUpdate();
            }
            br.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();

        } 
    }

    private void updateEmployeeSalaries(double percentage) 
    {
    	try
    	{
    		Connection conn = connectToDB();
    		percentage = percentage/100;
    		PreparedStatement ps = conn.prepareStatement("update constructorEmployee set salaryPerDay = salaryPerDay + ?*salaryPerDay \r\n" + 
    													 "where eid in (select eid from ConstructionEmployeeOverFifty)");
    		ps.setDouble(1, percentage);
    		ps.executeUpdate();
    		ps.close();
    		conn.close();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }


    public void updateAllProjectsBudget(double percentage) 
    {
    	try
    	{
    		Connection conn = connectToDB();
    		percentage = percentage/100;
    		PreparedStatement ps = conn.prepareStatement("update project set Budget = Budget + ?*Budget");
    		ps.setDouble(1, percentage);
    		ps.executeUpdate();
    		ps.close();
    		conn.close();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }


    private double getEmployeeTotalSalary() 
    {
    	try
    	{
    		Connection conn = connectToDB();
    		PreparedStatement ps = conn.prepareStatement("select sum(SalaryPerDay) as sumSalaries from constructorEmployee");
    		ResultSet rs = ps.executeQuery();
    		if(rs.next())
    		{
    			double sumSalaries = rs.getDouble("sumSalaries");
    			ps.close();
    			conn.close();
        		return sumSalaries;
    		}
    		ps.close();
    		conn.close();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return 0;
    }


    private int getTotalProjectBudget() 
    {
    	try
    	{
    		Connection conn = connectToDB();
    		PreparedStatement ps = conn.prepareStatement("select sum(Budget) as sumBudgets from Project");
    		ResultSet rs = ps.executeQuery();
    		if(rs.next())
    		{
    			int sumBudgets = rs.getInt("sumBudgets");
    			ps.close();
    			conn.close();
        		return sumBudgets;
    		}
    		ps.close();
    		conn.close();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return 0;
    }
    
    private void dropDB() 
    {
    	try
    	{
    		Connection conn = connectToDB();
    		PreparedStatement ps = conn.prepareStatement("use master; DROP DATABASE DB2019_Ass2");
    		ps.executeUpdate();
    		ps.close();
    		conn.close();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }

    private void initDB(String csvPath) 
    {
    	try {
    		String connectionString = "jdbc:sqlserver://localhost;instance=SQLEXPRESS;integratedSecurity=true;";
        	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        	Connection conn;
        	conn = DriverManager.getConnection(connectionString);
    		BufferedReader in = new BufferedReader(new FileReader(csvPath));
    		String str;
    		StringBuffer sb = new StringBuffer();
    		while ((str = in.readLine()) != null) {
    			if(str.equals("GO") || str.equals("go"))
    			{
    				PreparedStatement ps = conn.prepareStatement(sb.toString());
    	    		ps.execute();
    	    		sb = new StringBuffer();
    			}
    			else
    			{
    				sb.append(str + "\n ");
    			}
    		}
    		in.close();
    		PreparedStatement ps = conn.prepareStatement(sb.toString());
    		ps.execute();
    		ps.close();
    		conn.close();
    	} 
    	catch (Exception e) 
    	{
    		e.printStackTrace();
    	} 
    }
    
    private int calculateIncomeFromParking(int year) 
    {
    	try
    	{
    		Connection conn = connectToDB();
    		PreparedStatement ps = conn.prepareStatement("select sum(Cost) as yearlyCost from carParking where year(EndTime) = ?");
    		ps.setInt(1, year);
    		ResultSet rs = ps.executeQuery();
    		if(rs.next())
    		{
    			int yearlyCost = rs.getInt("yearlyCost");
    			ps.close();
    			conn.close();
        		return yearlyCost;
    		}
			ps.close();
    		conn.close();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return 0;
    }

    private ArrayList<Pair<Integer, Integer>> getMostProfitableParkingAreas() 
    {
    	try
    	{
    		Connection conn = connectToDB();
    		PreparedStatement ps = conn.prepareStatement("select top 5 PA.AID, sum(coalesce(CP.Cost,0)) as totalCost \r\n" + 
    				"from ParkingArea PA left outer join carParking CP on PA.AID = Cp.ParkingAreaID\r\n" + 
    				"group by PA.AID\r\n" + 
    				"order by sum(coalesce(CP.Cost,0)) desc");
    		ResultSet rs = ps.executeQuery();
    		ArrayList<Pair<Integer, Integer>> res = new ArrayList<Pair<Integer, Integer>>();
    		while(rs.next())
    		{
    			Pair<Integer, Integer> p = new Pair<Integer, Integer>(rs.getInt(1), rs.getInt(2));
    			res.add(p);
    		}
    		ps.close();
    		conn.close();
    		return res;
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return null;
    }

    private ArrayList<Pair<Integer, Integer>> getNumberOfParkingByArea() 
    {
    	try
    	{
    		Connection conn = connectToDB();
    		PreparedStatement ps = conn.prepareStatement("select PA.AID as ParkingAreaID, coalesce(countCarParkings,0) as parkingNumber\r\n" + 
    				"from ParkingArea PA left outer join (select parkingAreaID, count(*) as countCarParkings\r\n" + 
    				"from carParking \r\n" + 
    				"group by parkingAreaID) CountParkings on PA.AID = CountParkings.ParkingAreaID");
    		ResultSet rs = ps.executeQuery();
    		ArrayList<Pair<Integer, Integer>> res = new ArrayList<Pair<Integer, Integer>>();
    		while(rs.next())
    		{
    			Pair<Integer, Integer> p = new Pair<Integer, Integer>(rs.getInt(1), rs.getInt(2));
    			res.add(p);
    		}
    		ps.close();
    		conn.close();
    		return res;
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return null;
    }


    private ArrayList<Pair<Integer, Integer>> getNumberOfDistinctCarsByArea() 
    {
    	try
    	{
    		Connection conn = connectToDB();
    		PreparedStatement ps = conn.prepareStatement("select PA.AID as ParkingAreaID, coalesce(DistinctCarsNumber, 0) as DistinctCarsNumber\r\n" + 
    				"from ParkingArea PA left outer join\r\n" + 
    				"(select parkingAreaID, count(distinct CID) as DistinctCarsNumber\r\n" + 
    				"from carParking \r\n" + 
    				"group by parkingAreaID) DistinctParkings on PA.AID = DistinctParkings.ParkingAreaID");
    		ResultSet rs = ps.executeQuery();
    		ArrayList<Pair<Integer, Integer>> res = new ArrayList<Pair<Integer, Integer>>();
    		while(rs.next())
    		{
    			Pair<Integer, Integer> p = new Pair<Integer, Integer>(rs.getInt(1), rs.getInt(2));
    			res.add(p);
    		}
    		ps.close();
    		conn.close();
    		return res;
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return null;
    }


    private void AddEmployee(int EID, String LastName, String FirstName, Date BirthDate, String StreetName, int Number, int door, String City) 
    {
    	try
    	{
    		Connection conn = connectToDB();
    		PreparedStatement ps = conn.prepareStatement("insert into employee(EID, LastName, FirstName, BirthDate, StreetName, Number, door, City)\r\n" + 
    				"values(?, ?, ?, ?, ?, ?, ?, ?)");
    		ps.setInt(1, EID);
    		ps.setString(2, LastName);
    		ps.setString(3, FirstName);
    		ps.setDate(4,  BirthDate);
    		ps.setString(5, StreetName);
    		ps.setInt(6, Number);
    		ps.setInt(7, door);
    		ps.setString(8, City);
    		ps.executeUpdate();
    		ps.close();
    		conn.close();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
