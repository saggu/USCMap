package dbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	
	 Connection connDB;
	    
	    public DBConnection(){
	        
	        createConnection();
	    }
	    
	    
	private void createConnection() //create a connection to the database
	{
	    
	    try
	    {
	        
	        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
	        
	        String URL = "jdbc:oracle:thin:@//localhost:1521/<DataBaseName>";
	        String userName = "<USERNAME>";
	        String password = "<PASSWORD>";
	        
	        Statement stmtInsert = null;
	        
	        connDB = DriverManager.getConnection(URL, userName, password);
	        
	    }
	    catch(SQLException e){
	        
	        System.out.println("Exception in createConnection: " + e.toString());
	    }
	}


	public ResultSet getSpatialData (String query) //execute the query and result the ResultSet
	{
	    
	    try{
	        Statement stmt = connDB.createStatement();
	    
	        ResultSet rs = stmt.executeQuery(query);

	        return rs;
	    }
	    catch(SQLException e){
	        System.out.println("Exception in getSpatialData: " + e.toString());
	        return null;
	    }
	           
	}

}
