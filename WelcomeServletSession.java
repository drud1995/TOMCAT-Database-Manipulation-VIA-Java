// A simple servlet to process get requests.

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;

////////////////////
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;
import java.util.Properties;
import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import javax.swing.*;
//////////////////////

public class WelcomeServletSession extends HttpServlet {   
    
	private DisplayTable useTable;
	private Connection connection = null;
	
	// default query retrieves all data from bikes table
    static final String DEFAULT_QUERY = "select * from suppliers";
			//public int countHold = 0;
	
	// process "get" requests from clients
    protected void doGet( HttpServletRequest request, 
                         HttpServletResponse response ) throws ServletException, IOException
    {
	  //////////////////////
	  //without a properties file include these statements
				MysqlDataSource dataSource = new MysqlDataSource();
				
				String chosenJDBC = "com.mysql.cj.jdbc.Driver";
				
				//System.out.println("chosenJDBC is: " + chosenJDBC);
				try 
				{
					Class.forName(chosenJDBC);
				} 
				catch (ClassNotFoundException e2) 
				{
					e2.printStackTrace();
				}
				
				dataSource.setUser("root");
				dataSource.setPassword("Papermario2001@");
				
				dataSource.setURL("jdbc:mysql://localhost:3306/Project4");
				
				try 
				{
					connection = dataSource.getConnection();
				} 
				catch (SQLException e1) 
				{
					e1.printStackTrace();
				}
	  //////////////////////
	  
	  Statement statement = null;
	try {
		statement = connection.createStatement();
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
		
      String inputSQL = request.getParameter("inputSQL");
	  String outputSQL = null;
	  
	////////////////////////////////////
	try 
	{
		useTable = new DisplayTable(inputSQL, connection);
	} 
	catch (ClassNotFoundException e) 
	{
		e.printStackTrace();
	} 
	catch (SQLException e) 
	{
		e.printStackTrace();
	}
	////////////////////////////////////
	  
	  /////////////////////////////
		//string to determine whether to query or update
		String[] checkerString = inputSQL.split(" ");
		
		try
		{
			
			//check for query case
			if((checkerString[0].equals("select")) || (checkerString[0].equals("select*")) || (checkerString[0].equals("SELECT")) || (checkerString[0].equals("SELECT*")))
			{
				useTable.setQuery(inputSQL);
				//////////////////////////////
				outputSQL += "<table border =" + "'1'" + "align = " + "'center'" + ">";
				
				outputSQL += "<tr>";
				
				//Get the column headers for the HTML table
				for(int x = 0; x < useTable.getColumnCount(); x++)
				{
					outputSQL += "<th bgcolor=" + "'#FF0000'" + " >";
					outputSQL += "<font color = " + "'#000000'" + " >";
					if (useTable.getColumnName(x) != null)
						outputSQL += useTable.getColumnName(x);
					outputSQL += "</font>";
					outputSQL += "</th>";
				}
				
				outputSQL += "</tr>";
				
				int colorSwitcher = 1;
				
				for (int x = 0;  x < useTable.getRowCount(); x++)
				{
					outputSQL += "<tr  bgcolor = ";
					
					if (colorSwitcher == 1)
					{
						outputSQL += "'#d9d9d9'";
						colorSwitcher--;
					}
					else
					{
						outputSQL += "'#f2f2f2'";
						colorSwitcher++;
					}
						
					outputSQL += " >";
					
					for(int y = 0; y < useTable.getColumnCount(); y++)
					{
						outputSQL += "<td>";
						
						
						
						outputSQL += "<font color = " + "'#000000'" + " >";
						
						outputSQL += useTable.getValueAt(x,y).toString();
						
						outputSQL += "</font>";
						
						outputSQL += "</td>";
					}
					
					outputSQL += "</tr>";
				}
				
				outputSQL += "</table>";
				//////////////////////////////
			}
			//check for update case
			else if((checkerString[0].equals("INSERT")) || (checkerString[0].equals("UPDATE")) || (checkerString[0].equals("DELETE")) || (checkerString[0].equals("CREATE")) || (checkerString[0].equals("DROP")) || (checkerString[0].equals("insert")) || (checkerString[0].equals("update")) || (checkerString[0].equals("delete")) || (checkerString[0].equals("create")) || (checkerString[0].equals("drop")))
			{
				useTable.setUpdate(inputSQL);
				/////////////////////////////
				int modified_value = statement.executeUpdate(inputSQL);
				
				outputSQL += "<div align = 'center'>";
			 
				outputSQL += "<div style = 'color: white; background-color: green; width: 400px; border: 5px solid black; padding: 50px; margin: 20px'>";
			 
				outputSQL += "The statement executed successfully. <br>";
			 
				outputSQL += Integer.toString(modified_value);
				
				outputSQL += " row(s) affected. <br>";
				
				outputSQL += "Business Logic Detected! - Updating Supplier Status <br>";
				
				outputSQL += "Business Logic updated " + Integer.toString(modified_value) + " supplier status marks.";
			 
				outputSQL += "</div>";
			 
				outputSQL += "</div>";
				
				/////////////////////////////
			}
			//neither case satisfied...
			else;
		}
	
		catch (SQLException sqlException)
		{
			 //JOptionPane.showMessageDialog( null, 
			 // sqlException.getMessage(), "Database error", 
			 //JOptionPane.ERROR_MESSAGE );
			 String errorMessage = sqlException.getMessage();
			 
			 outputSQL += "<div align = 'center'>";
			 
			 outputSQL += "<div style = 'color: white; background-color: red; width: 400px; border: 5px solid black; padding: 50px; margin: 20px'>";
			 
			 outputSQL += "<b> Error executing the SQL statement: </b> <br>";
			 
			 outputSQL += errorMessage;
			 
			 outputSQL += "</div>";
			 
			 outputSQL += "</div>";
			 // try to recover from invalid user query 
			 // by executing default query
			 try 
			 {
				useTable.setQuery( DEFAULT_QUERY );
				//queryArea.setText( DEFAULT_QUERY );
			 } // end try
			 catch ( SQLException sqlException2 ) 
			 {
				JOptionPane.showMessageDialog( null, 
				   sqlException2.getMessage(), "Database error", 
				   JOptionPane.ERROR_MESSAGE );
 
				// ensure database connection is closed
				useTable.disconnectFromDatabase();
 
				System.exit( 1 ); // terminate application
			 } // end inner catch                   
		}
	
	  /////////////////////////////
	  
      
      //outputSQL += "<span id='servlet'>servlet </span> and <span id='jsp'>jsp </span>technology.";
      HttpSession session = request.getSession();
      session.setAttribute("outputSQL",  outputSQL);
      session.setAttribute("inputSQL", inputSQL);
      RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/welcome.jsp");
      dispatcher.forward(request, response);      
    }   //end doGet() method
    
    public void doPost(HttpServletRequest request, HttpServletResponse response )
    	throws IOException, ServletException {
    		doGet(request, response);
        	}

} //end WelcomeServlet class



