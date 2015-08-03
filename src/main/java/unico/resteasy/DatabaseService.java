package unico.resteasy;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * <li>ORM (Database) Service. Very simple, one table, one column inserts so 
 * implementation using embedded JBoss H2 implementation (for now) and basic.
 * The core JDBC components are referenced as single member variables 
 * per instance of this service. The service <b>should not be</b> shared between clients 
 * for that reason; rather new instances created.
 * 
 * <b>Note:</b>Primary keys are <i>not</i> used due to the specific nature of the business logic, 
 * ie order is kept by insertion and is never altered from the first in last out workflow.
 * 
 * <li>Due to the simplistic nature of the implementation, it was decided an ORM like Hibernate 
 * or Spring wasn't required.
 * @author K. Flattery
 */
@Startup
@Singleton
public class DatabaseService {

	public final static String TABLE = "QUEUE_HISTORY";
	public final static String GCD_TABLE = "GCD_TABLE";
	//private static long primaryKey = 0L;
	private DataSource cf;
	private final static Logger LOGGER = Logger.getLogger(DatabaseService.class.getName());
	
	/**
	 * Constructor; creates tables if they aren't in existance.
	 */
	public DatabaseService() {
		try {
			Context ic = new InitialContext();
			cf = (DataSource) ic.lookup("java:jboss/datasources/ExampleDS");	
			if (!tableExists(TABLE)) {
				createTable(TABLE);
			}
			if (!tableExists(GCD_TABLE)) {
				createTable(GCD_TABLE);
			}			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param value to be added to persistent store with incremented priamry key.
	 * @return boolean result from {@link PreparedStatement#execute()} ie true <i>iff</i> success.
	 */
	public boolean add(String value, String table) {
		try {
			Connection connection = cf.getConnection();
			PreparedStatement pS = connection.prepareStatement("insert into " + 
			table + " (value) values (?)");
			// pS.setLong(0, primaryKey++);
			pS.setInt(1, Integer.valueOf(value));
			boolean b = pS.execute();
			connection.close();
			return b;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			//connection.close(); //TODO update to resource to avoid explicit closure
		}
	}

	/**
	 * No need for a primary key as data is only to reported as entered <i>only</i>
	 * @throws SQLException for any reason table cannot be created.
	 */
	private void createTable(String table) throws SQLException {
		Connection connection = cf.getConnection();
		Statement s = connection.createStatement();
		s.executeUpdate("create table " + table + " (value number(3)) ");
		connection.close();
		LOGGER.info("Created " + table + " in database.");
	}

	private boolean tableExists(String table) throws SQLException {
		Connection connection = cf.getConnection();
		ResultSet rS = connection.getMetaData().getTables(null, null, table, null);
		boolean b = (rS.next() && rS.getString(3).equals(table));
		connection.close();
		LOGGER.finer("tableExists: " + b);
		return b;
	}

	/** @return full lists if integers as they are ordered in the database */
	public List<Integer> getQueueAudit() throws SQLException {
		return getAllRows(TABLE);
	}
	
	/** @return full lists if integers as they are ordered in the database */
	public List<Integer> getGCDAudit() throws SQLException {
		return getAllRows(GCD_TABLE);
	}
	
	private List<Integer> getAllRows(String table) throws SQLException {
		Connection connection = cf.getConnection();
		if (!tableExists(table)) {
			throw new SQLException(table + " does not exist!");
		}
		Statement s = connection.createStatement();
		ResultSet rS = s.executeQuery("select value from " + table);
		List<Integer> list = new ArrayList<Integer>();
		while (rS.next()) {
			list.add(new Integer(rS.getInt(1)));
		}
		connection.close();
		return list;
	}

	public void addGCD(int k) {
		add(String.valueOf(k), GCD_TABLE);		
	}
}
