package dbc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class PopulateDB {
	
public static void main(String[] args)  {
        
        try
        {
            if(args.length < 3)
            {
                System.out.println("Provide all the files, Usage : buildings.xy students.xy announcementSystems.xy");
            }
            else
            {
                PopulateDB ppl = new PopulateDB();

                String strFile1 = args[0].trim();
                String strFile2 = args[1].trim();
                String strFile3 = args[2].trim();


                String deleteBuildingQuery = "delete from buildings"; //delete from table buildings
                String deleteStudentsQuery = "delete from students"; //delete from ta
                String deleteAnsysQuery     = "delete from ansys";  ////delete from table ansys

                System.out.println("Deleting data from database");
                ppl.insertQueries(deleteBuildingQuery);
                ppl.insertQueries(deleteStudentsQuery);
                ppl.insertQueries(deleteAnsysQuery);
                System.out.println("Deletion Complete");

                ppl.openFileAndInsertData(strFile1);
                ppl.openFileAndInsertData(strFile2);
                ppl.openFileAndInsertData(strFile3);

                System.out.println("Completed!");
            }
           
            
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        
   
    }
    
    public void openFileAndInsertData(String fileName) 
    {
        try
        {
            
            File file = new File(fileName);
           
            FileReader fr = new FileReader(file);
        
            BufferedReader br = new BufferedReader(fr);
            
            String query  = "";
            
            
            switch (file.getName())
            {
                case "buildings.xy" :
                    
                    System.out.println("Reading file : " +file.getName());
                    
                     while(br.ready())
                     {
                         List<String> strPoints = Arrays.asList(br.readLine().split(","));
                         
                         String ordinates = "";
                         
                         for(int i=3; i <= strPoints.size()-1;  i++)
                         {
                             ordinates = ordinates + strPoints.get(i) + ",";
                             
                         }
                         
                         ordinates = ordinates + strPoints.get(3) + "," + strPoints.get(4);
                         
                         
                         query = "INSERT INTO buildings VALUES(" +
                                 "'" + strPoints.get(0)+ "'," +
                                 "'" + strPoints.get(1) + "'," +
                                 strPoints.get(2) + 
                                 ",SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(" +
                                 ordinates + ")))";

                            System.out.println(query);
                            
                            insertQueries(query);
                      }
                break;
                    
                case "students.xy" :
                    
                    System.out.println("Reading file : " +file.getName());
                    
                     while(br.ready())
                     {
                         List<String> strPoints = Arrays.asList(br.readLine().split(","));
                         query = "INSERT INTO students VALUES( " +
                                 "'" + strPoints.get(0) +"', " +
                                 "SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(" +
                                 strPoints.get(1) + ", " + strPoints.get(2) + ", NULL),NULL,NULL))";

                            System.out.println(query);
                            
                            insertQueries(query);
                      }
                break;
            
                      
                    
                case "announcementSystems.xy" :
                    
                    while(br.ready())
                    {
                          System.out.println("Reading file : " +file.getName());
                          
                          List<String> strPoints = Arrays.asList(br.readLine().split(","));
                          
                          int radius = Integer.parseInt(strPoints.get(3).trim());
                          int x = Integer.parseInt(strPoints.get(1).trim());
                          int y = Integer.parseInt(strPoints.get(2).trim());
                          
                          int y1 = y + radius;
                          int y2 = y - radius ;
                          int x1 = x + radius ;
                          
                          query = "INSERT INTO ansys VALUES( " +
                                  "'" + strPoints.get(0) + "', " +
                                  "SDO_GEOMETRY( 2003, NULL, NULL, SDO_ELEM_INFO_ARRAY(1,1003,4),SDO_ORDINATE_ARRAY(" +
                                  x + "," + y1 + "," + x+ "," + y2 + "," + x1 + "," + y + "))," + radius + " )";
                          
                          System.out.println(query);
                          
                         insertQueries(query);

                          
                    }
                break;
                
                default :
                    System.out.println("Please check the file names!");
                break;

           
            }
           
         br.close();
         
         
         
         
        }
        catch(IOException e)
        {
            System.out.println(e.toString());
            
        }
    }
    
    public void insertQueries(String query)
    {
        try
        {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());

            String URL = "jdbc:oracle:thin:@//localhost:1521/TWITTER";
            String userName = "TWITTER";
            String password = "ROOT";

            Statement stmtInsert;
            Connection connDB;

            connDB = DriverManager.getConnection(URL, userName, password);

            stmtInsert = connDB.createStatement();
            stmtInsert.executeUpdate(query);
            
            connDB.close();
        }
        catch(SQLException e)
        {
            System.out.println(e.toString());
        }
    }

}
