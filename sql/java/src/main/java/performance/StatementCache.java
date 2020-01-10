package performance;

import com.informix.jdbc.IfmxStatement;
import com.informix.jdbc.IfxSqliConnect;
import com.informix.jdbc.PreparedStatementCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

/**
 * Statment caching is a way to improve repeated PreparedStatement performance by
 * caching the statements inside the driver.  This allows the use of try-with-resources
 * blocks in java while not having to manually cache the prepared statements elsewhere
 *
 * Enabling the server side STMT_CACHE provides additional benefits as the server side query
 * plan is shared amongst all the connections that use the same query
 */
public class StatementCache {
	private static final Logger logger = LoggerFactory.getLogger(StatementCache.class);
	
	public static void main(String[] args) throws SQLException {
		if(args.length != 1) {
			throw new NullPointerException("You must pass the connection URL as the first argument to the demo");
		}
		
		/*
		 * By default the statement cache is turned off.  You enable it per connection
		 *  by specifying the size of the cache. the size is per connection so if you have
		 *  a connection pool of 10 connections and you cache 20 prepared statements, you will
		 *  have 200 prepared statement objects stored in client memory. They are not massive but
		 *  can add up in size.
		 */
		Properties p = new Properties();
		p.setProperty("IFX_PREPAREDSTATEMENT_CACHE_SIZE", "2");
		try (IfxSqliConnect c = (IfxSqliConnect) DriverManager.getConnection(args[0], p)) {
			PreparedStatement pStmt = c.prepareStatement("SELECT * FROM systables");
			pStmt.close();
			
			PreparedStatement pStmt2 = c.prepareStatement("SELECT * FROM systables");
			pStmt2.close();
			// We should get the exact same object back in this case.
			assert pStmt == pStmt2;
			PreparedStatementCache cache = c.getPreparedStatementCache();
			assert cache.size() == 1;
			
			// This is a different statement, so it's added to the cache
			PreparedStatement pStmt3 = c.prepareStatement("SELECT tabid FROM systables");
			pStmt3.close();
			
			assert cache.size() == 2;
			
			// This is a different statement, but the cache size is only 2 so one has to go
			PreparedStatement pStmt4 = c.prepareStatement("SELECT tabname FROM systables");
			pStmt4.close();
			
			assert cache.size() == 2;
			
			// printing out the cached statements SQL you can see the first prepared statement was evicted
			// since it was the oldest in the list.
			logger.info("Cached Prepared SQL: {}", cache.keySet());
		}
	}
}
