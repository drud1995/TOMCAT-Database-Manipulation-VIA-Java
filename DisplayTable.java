import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import java.util.Properties;
import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;


public class DisplayTable extends AbstractTableModel
{
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private ResultSetMetaData metaData;
	private int numberOfRows;
	
	
	//ensure connection to database...
	private boolean DatabaseConnection = false;
	
	public DisplayTable (String query, Connection inputConnection) throws SQLException, ClassNotFoundException
	{
		
		String[] checkerString1 = query.split(" ");
		//get already established connection as input
		Connection connection = inputConnection;
		
		//set up statement for querying purposes
		statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		//update your database checker after inputting already established connection
		DatabaseConnection = true;
		
		
		//setQuery(query);
		
		//check for query case
		if((checkerString1[0].equals("select")) || (checkerString1[0].equals("select*")) || (checkerString1[0].equals("SELECT")) || (checkerString1[0].equals("SELECT*")))
			setQuery(query);
		//check for update case
		else if((checkerString1[0].equals("INSERT")) || (checkerString1[0].equals("UPDATE")) || (checkerString1[0].equals("DELETE")) || (checkerString1[0].equals("CREATE")) || (checkerString1[0].equals("DROP")) || (checkerString1[0].equals("insert")) || (checkerString1[0].equals("update")) || (checkerString1[0].equals("delete")) || (checkerString1[0].equals("create")) || (checkerString1[0].equals("drop")))
			setUpdate(query);
		//neither case satisfied...
		else
			JOptionPane.showMessageDialog(null, "Invalid SQL Statement Entered");

	}
	
   // get number of columns in ResultSet
   public int getColumnCount() throws IllegalStateException
   {   
      // ensure database connection is available
      if ( !DatabaseConnection ) 
         throw new IllegalStateException( "Not Connected to Database" );

      // determine number of columns
      try 
      {
         return metaData.getColumnCount(); 
      } // end try
      catch ( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
      } // end catch
      
      return 0; // if problems occur above, return 0 for number of columns
   } // end method getColumnCount

   // return number of rows in ResultSet
   public int getRowCount() throws IllegalStateException
   {      
      // ensure database connection is available
      if ( !DatabaseConnection ) 
         throw new IllegalStateException( "Not Connected to Database" );
 
      return numberOfRows;
   } // end method getRowCount

   // obtain value in particular row and column
   public Object getValueAt( int row, int column )  throws IllegalStateException
   {
      // ensure database connection is available
      if ( !DatabaseConnection ) 
         throw new IllegalStateException( "Not Connected to Database" );

      // obtain a value at specified ResultSet row and column
      try 
      {
		   resultSet.next();  /* fixes a bug in MySQL/Java with date format */
         resultSet.absolute( row + 1 );
         return resultSet.getObject( column + 1 );
      } // end try
      catch ( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
      } // end catch
      
      return ""; // if problems, return empty string object
   } // end method getValueAt
	
	// get class that represents column type
   public Class getColumnClass( int column ) throws IllegalStateException
   {
      // ensure database connection is available
      if ( !DatabaseConnection ) 
         throw new IllegalStateException( "Not Connected to Database" );

      // determine Java class of column
      try 
      {
         String className = metaData.getColumnClassName( column + 1 );
         
         // return Class object that represents className
         return Class.forName( className );
      } // end try
      catch ( Exception exception ) 
      {
         exception.printStackTrace();
      } // end catch
      
      return Object.class; // if problems occur above, assume type Object
   } // end method getColumnClass
   
   
   public String getColumnName( int column ) throws IllegalStateException
   {    
      // ensure database connection is available
      if ( !DatabaseConnection ) 
         throw new IllegalStateException( "Not Connected to Database" );

      // determine column name
      try 
      {
         return metaData.getColumnName( column + 1 );  
      } // end try
      catch ( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
      } // end catch
      
      return ""; // if problems, return empty string for column name
   } // end method getColumnName
   
   // set new database query string
   public void setQuery( String query ) 
      throws SQLException, IllegalStateException 
   {
      // ensure database connection is available
      if ( !DatabaseConnection ) 
         throw new IllegalStateException( "Not Connected to Database" );

      // specify query and execute it
      resultSet = statement.executeQuery( query );

      // obtain meta data for ResultSet
      metaData = resultSet.getMetaData();

      // determine number of rows in ResultSet
      resultSet.last();                   // move to last row
      numberOfRows = resultSet.getRow();  // get row number      
      
      // notify JTable that model has changed
      fireTableStructureChanged();
   } // end method setQuery


// set new database update-query string
   public void setUpdate( String query ) 
      throws SQLException, IllegalStateException 
   {
	  int res;
      // ensure database connection is available
      if ( !DatabaseConnection ) 
         throw new IllegalStateException( "Not Connected to Database" );

      // specify query and execute it
      res = statement.executeUpdate( query );
/*
      // obtain meta data for ResultSet
      metaData = resultSet.getMetaData();
      // determine number of rows in ResultSet
      resultSet.last();                   // move to last row
      numberOfRows = resultSet.getRow();  // get row number      
*/    
      // notify JTable that model has changed
      fireTableStructureChanged();
   } // end method setUpdate

   // close Statement and Connection               
   public void disconnectFromDatabase()            
   {              
      if ( !DatabaseConnection )                  
         return;
      // close Statement and Connection            
      try                                          
      {                                            
         statement.close();                        
         connection.close();                       
      } // end try                                 
      catch ( SQLException sqlException )          
      {                                            
         sqlException.printStackTrace();           
      } // end catch                               
      finally  // update database connection status
      {                                            
         DatabaseConnection = false;              
      } // end finally                             
   } // end method disconnectFromDatabase     

}
