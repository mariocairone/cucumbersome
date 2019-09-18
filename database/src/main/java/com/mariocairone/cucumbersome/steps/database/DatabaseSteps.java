package com.mariocairone.cucumbersome.steps.database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.jakewharton.fliptables.FlipTableConverters;
import com.mariocairone.cucumbersome.steps.BaseStepDefs;
import com.mariocairone.cucumbersome.template.aspect.ParseArgs;
import com.mariocairone.cucumbersome.template.parser.TemplateParser;
import com.mariocairone.cucumbersome.utils.AssertionUtils;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

@SuppressWarnings("deprecation")
public class DatabaseSteps extends BaseStepDefs {

	private String databaseUrl;
	private String databaseUsername;
	private String databasePassword;
	private String databaseSchema;
	private String databaseCatalog;	
	
	private List<Map<String,Object>> results;
		
    public DatabaseSteps(TemplateParser parser) throws SQLException {
		super(parser);
		DatabaseConfig config = DatabaseConfig.databaseOptions();
		//setDatabaseCatalog(config.getDatabaseCatalog());
		setDatabaseUrl(config.getDatabaseUrl());
		setDatabaseUsername(config.getDatabaseUsername());
		setDatabasePassword(config.getDatabasePassword());
		//setDatabaseSchema(config.getDatabaseSchema());
    }    



	protected void insert(final String tableName, final List<List<String>> rows) throws SQLException {
    	
    	final List<String> columns = rows.get(0);
    	
    	Map<String, Integer> columnMapping = getColumnTypes(databaseCatalog, databaseSchema, tableName);
        final StringBuilder queryBuilder = new StringBuilder();
        
        List<String> params =columns.stream().map( entry -> "?").collect(Collectors.toList());
     
        queryBuilder.append(String.format("INSERT INTO %s (", tableName));
        queryBuilder.append(String.join(",", columns));
        queryBuilder.append(") VALUES (" );   
        queryBuilder.append(String.join(",", params) + ")");

        try (Connection conn = getConnection()) {
        	String sql = queryBuilder.toString();
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
	            for (List<String> row : rows.subList(1, rows.size())) {
	                for (int i = 0; i < columns.size(); i++) {
	                	int type = columnMapping.get(columns.get(i));             
	                    stmt.setObject(i + 1, row.get(i),type);
	                }
	                 stmt.executeUpdate();
	            }
            }
        } 
	
    }

    protected void deleteAll(final String tableName) throws SQLException {
    	String sql = "DELETE FROM " + tableName;
		try(PreparedStatement preparedStatement = getConnection().prepareStatement(sql)){
			preparedStatement.execute();
		}
    }

    protected void exists(final String tableName, final List<List<String>> rows) throws SQLException,
            ClassNotFoundException {
 	    	
    	final List<String> columns = rows.get(0);
    	Map<String, Integer> columnMapping = getColumnTypes(databaseCatalog, databaseSchema, tableName);
        final StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(String.format("SELECT %s FROM %s WHERE ", StringUtils.join(columns, ","),
                                          tableName));
        queryBuilder.append(StringUtils.join(columns, " = ? AND "));
        queryBuilder.append(" = ?;");
        try (Connection conn = getConnection()) {
        	String sql = queryBuilder.toString();
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
            for (List<String> row : rows.subList(1, rows.size())) {
                for (int i = 0; i < columns.size(); i++) {
                	int type = columnMapping.get(columns.get(i));             
                    stmt.setObject(i + 1, row.get(i),type);
                }
                final ResultSet rs = stmt.executeQuery();
                assertTrue(rs.next());
            }
            }
        }
    }

    protected void notExists(final String tableName, final List<List<String>> rows) throws SQLException,
    ClassNotFoundException {
 	
		final List<String> columns = rows.get(0);
		Map<String, Integer> columnMapping = getColumnTypes(databaseCatalog, databaseSchema, tableName);
		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(String.format("SELECT %s FROM %s WHERE ", StringUtils.join(columns, ","),
		                                  tableName));
		queryBuilder.append(StringUtils.join(columns, " = ? AND "));
		queryBuilder.append(" = ?;");
		try (Connection conn = getConnection()) {
			String sql = queryBuilder.toString();
		   try( PreparedStatement stmt = conn.prepareStatement(sql)){
		    for (List<String> row : rows.subList(1, rows.size())) {
		        for (int i = 0; i < columns.size(); i++) {
		        	int type = columnMapping.get(columns.get(i));             
		            stmt.setObject(i + 1, row.get(i),type);
		        }
		        final ResultSet rs = stmt.executeQuery();
		        assertFalse(rs.next());
		    }
		   }
		}
}
    
    protected Map<String, Integer> getColumnTypes(String catalog,String schema,String tableName) throws SQLException {
    	
    	Map<String, Integer> mapping =  new HashMap<>();
    	
		ResultSet rsColumns = getConnection().getMetaData().getColumns(catalog, schema, tableName, "%");
		
	    while (rsColumns.next()) {
	        String columnName = rsColumns.getString("COLUMN_NAME");		        
	        int dataType = rsColumns.getInt( "DATA_TYPE" );
	        mapping.put(columnName, dataType);

	   }
	    
	    return mapping;    	
    }

    protected void executeRawSql(List<String> sqlStatements) throws SQLException {
	    for(String sql : sqlStatements){
            try(PreparedStatement preparedStatement = getConnection().prepareStatement(sql)){
            preparedStatement.execute();
            }
	    }
	}
	
	
    protected Connection getConnection() throws SQLException {
    	    	
		Connection connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
		if(connection != null) {
			this.databaseSchema = connection.getSchema();
			this.databaseCatalog = connection.getCatalog();
		}
		return connection;
	}


    
	public List<Map<String,Object>> resultSetToArrayList(ResultSet rs) throws SQLException{
		  ResultSetMetaData md = rs.getMetaData();
		  int columns = md.getColumnCount();
		  ArrayList<Map<String,Object>> list = new ArrayList<>(50);
		  while (rs.next()){
		     HashMap<String,Object> row = new HashMap<>(columns);
		     for(int i=1; i<=columns; ++i){           
		      row.put(md.getColumnName(i),rs.getObject(i));
		     }
		      list.add(row);
		  }

		 return list;
	}
	
	public String resultSetToStringTable(ResultSet resultSet) throws Exception {
	    if (resultSet == null) throw new NullPointerException("resultSet == null");
	    if (!resultSet.isBeforeFirst()) throw new IllegalStateException("Result set not at first.");

	    List<String> headers = new ArrayList<>();
	    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
	    int columnCount = resultSetMetaData.getColumnCount();
	    for (int column = 0; column < columnCount; column++) {
	      headers.add(resultSetMetaData.getColumnName(column + 1));
	    }

	    List<Object[]> data = new ArrayList<>();
	    while (resultSet.next()) {
	    	Object[] rowData = new Object[columnCount];
	      for (int column = 0; column < columnCount; column++) {
	        rowData[column] = resultSet.getObject(column + 1);
	      }
	      data.add(rowData);
	    }

	    String[] headerArray = headers.toArray(new String[headers.size()]);
	    Object[][] dataArray = data.toArray(new Object[data.size()][]);
	    
	    
	    return FlipTableConverters.fromObjects(headerArray, dataArray);
	}
	

    @ParseArgs
	@Given("^the database username is \"([^\"]*)\"$")
	public void setDatabaseUsername(String username)
	{    	
		this.databaseUsername = username;
   };      
   
    @ParseArgs   
	@Given("^the database password is \"([^\"]*)\"$")
	public void setDatabasePassword(String password)
	{    	
		this.databasePassword = password;
    }

    @ParseArgs  
	@Given("^the database url is \"([^\"]*)\"$")
	public void setDatabaseUrl(String url)
	{    	
		this.databaseUrl = url;
	}   

 
    @ParseArgs  
	@Given("^the database schema is \"([^\"]*)\"$")    
    public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}

    @ParseArgs  
	@Given("^the database catalog is \"([^\"]*)\"$")
	public void setDatabaseCatalog(String databaseCatalog) {
		this.databaseCatalog = databaseCatalog;
	}



	@ParseArgs
	@Given("^the database table \"([^\"]*)\" is empty$")
	public void theDatabaseTableIsEmpty(String tableName) throws SQLException
	{    	
		this.deleteAll(tableName);
	}   

    @ParseArgs	
	@Given("^the database query below is executed:$")
	public void theDatabaseQueryIsExecuted(String query) throws SQLException
	{    	
		
	 PreparedStatement preparedStatement	= null	;
		 try {		   									
			preparedStatement = getConnection().prepareStatement(query);

		    ResultSet rs = preparedStatement.executeQuery();
			results = resultSetToArrayList(rs);
					   				
		 } finally {
			 if(preparedStatement != null)
				 preparedStatement.close();
		}
	}
    
    @ParseArgs	
	@Given("^the database query results are stored in the variable \"([^\"]*)\"$")
	public void theDatabaseQueryResultsAreStoredInAvariable(String varName) throws SQLException
	{    	
			variables.put(varName, results); 
	}
    
    @ParseArgs
	@Given("^the database table \"([^\"]*)\" exists$")
	public void theDatabaseTableExists(String tableName) throws SQLException
	{    	

    	DatabaseMetaData dbm = getConnection().getMetaData();
    	// check if table is there
    	ResultSet tables = dbm.getTables(databaseCatalog, databaseSchema, tableName, null);
    	assertTrue(tables.next());	    
	}
	
    @ParseArgs
	@Given("^the database script \"([^\"]*)\" is executed$")
	public void theDatabaseScriptIsExecuted(String script) throws SQLException, IOException
	{    	
        String sql = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(script), "UTF-8");
       
        sql = parser.parse(sql);
        
        executeRawSql(Collections.singletonList(sql));	    
	}
    
    @ParseArgs
	@Then("^the database table \"([^\"]*)\" should contain(?: (less than|more than|at least|at most))? (\\d+) row(?:s)?$")
	public void theDatabasetableShouldContain(String tableName,String comparisonAction, Integer count) throws SQLException, IOException
	{    	
		PreparedStatement statementNumEntriesWithAttribute	= null	;
		 try {	
					
				String sql = "SELECT COUNT(*) FROM " +  tableName ;
				statementNumEntriesWithAttribute = getConnection().prepareStatement(sql);

				ResultSet rs = statementNumEntriesWithAttribute.executeQuery();
				rs.next();
		 
		 
			AssertionUtils.compareCounts(Optional.ofNullable(comparisonAction).orElse(""), count, rs.getInt(1));
			
		 } finally {
			 if(statementNumEntriesWithAttribute != null)
				statementNumEntriesWithAttribute.close();
		}   
	}
    
    @ParseArgs	
	@Then("^the database table \"([^\"]*)\" should have the following row(?:s)?:$")
	public void theDatabaseTableShouldHaveTheFollowingRows(String tableName,List<List<String>> data) throws Exception
	{    	
		
    	exists(tableName, data); 
		
	}
    
    @ParseArgs	
	@Then("^the database table \"([^\"]*)\" should not have the following row(?:s)?:$")
	public void theDatabaseTableShouldNotHaveTheFollowingRows(String tableName,List<List<String>> data) throws Exception
	{    	
    	notExists(tableName, data); 		
	} 
    
    @ParseArgs
	@Then("^the database table \"([^\"]*)\" contains the following row(?:s)?:$")
	public void theDatabaseTableContainTheFollowingRows(String tableName,List<List<String>> data) throws Exception
	{    	

		this.insert(tableName, data);
	    		
	}
    
    @ParseArgs
	@Then("^the database table \"([^\"]*)\" contains only the following row(?:s)?:$")
	public void theDatabaseTableContainOnlyTheFollowingRows(String tableName,List<List<String>> data) throws Exception
	{    	

    	this.deleteAll(tableName);
        this.insert(tableName, data);
		
	}
    
    @ParseArgs
	@Then("^the database table \"([^\"]*)\" is printed$")
	public void theDatabaseTableIsPrinted(String tableName) throws Exception 
	{    	
		String query = "SELECT * FROM  " +tableName;
		
		     PreparedStatement preparedStatement	= null;
			 try {		   									
				preparedStatement = getConnection().prepareStatement(query);

			    ResultSet rs = preparedStatement.executeQuery();
				String table = resultSetToStringTable(rs);
				System.out.println(table);
				
			 } finally {
				 if(preparedStatement != null)
					 preparedStatement.close();
			}	
		
	} 		


	
	
}
