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
 * ORM (Database) Service. Very simple, one table, one column inserts so 
 * implementation using embedded JBoss H2 implementation (for now) and basic
 * <code>javax.sql<code> API.
 * @author K. Flattery
 */
@Startup
@Singleton
public class DatabaseService {

	private final static String TABLE = "QUEUE_HISTORY";
	//private static long primaryKey = 0L;
	private DataSource cf;
	private final static Logger LOGGER = Logger.getLogger(DatabaseService.class.getName());
	
	public DatabaseService() {
		try {
			Context ic = new InitialContext();
			cf = (DataSource) ic.lookup("java:jboss/datasources/ExampleDS");	
			if (!tableExists()) {
				createTable();
			}			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param value to be added to persistent store with incremented priamry key.
	 * @return boolean result from {@link PreparedStatement#execute()} ie true <i>iff</i> success.
	 */
	public boolean add(String value) {
		try {
			Connection connection = cf.getConnection();
			PreparedStatement pS = connection.prepareStatement("insert into " + 
			TABLE + " (value) values (?)");
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
	private void createTable() throws SQLException {
		Connection connection = cf.getConnection();
		Statement s = connection.createStatement();
		s.executeUpdate("create table " + TABLE + " (value number(3)) ");
		connection.close();
		LOGGER.info("Created " + TABLE + " in database.");
	}

	private boolean tableExists() throws SQLException {
		Connection connection = cf.getConnection();
		ResultSet rS = connection.getMetaData().getTables(null, null, TABLE, null);
		boolean b = (rS.next() && rS.getString(3).equals(TABLE));
		connection.close();
		LOGGER.info("tableExists: " + b);
		return b;
	}

	public List<Integer> getQueueAudit() throws SQLException {
		Connection connection = cf.getConnection();
		if (!tableExists()) {
			throw new SQLException(TABLE + " does not exist!");
		}
		Statement s = connection.createStatement();
		ResultSet rS = s.executeQuery("select value from " + TABLE);
		List<Integer> list = new ArrayList<Integer>();
		while (rS.next()) {
			list.add(new Integer(rS.getInt(1)));
		}
		connection.close();
		return list;
	}
}
