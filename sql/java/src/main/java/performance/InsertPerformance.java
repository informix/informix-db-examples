package performance;

import com.informix.jdbc.IfxSqliConnect;
import com.informix.jdbc.PreparedStatementCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Shows the differences in insert performance
 *
 * Attempts to show the relative insert performance between basic SQL statements,
 * PreparedStatements, PreparedStatement with batching, and using batching with IFX_USEPUT
 *
 * If the test runs too fast or too slow, adjust the INSERT_SIZE variable to a new size 
 */
public class InsertPerformance {
	private static final Logger logger = LoggerFactory.getLogger(InsertPerformance.class);
	
	private static long startTime = 0;
	private static long stopTime = 0;
	
	private static final int INSERT_SIZE = 10000;
	
	private static final Map<String, List<Long>> PERFORMANCE_MAP = new LinkedHashMap<>();
	
	public static void main(String[] args) throws SQLException {
		if(args.length != 1) {
			throw new NullPointerException("You must pass the connection URL as the first argument to the demo");
		}
		
		//warm up some java code first.
		for(int i = 0; i < 100; i++) {
			try(Connection c = DriverManager.getConnection(args[0])) {
				try(PreparedStatement p = c.prepareStatement("SELECT * FROM systables where tabid = ?")) {
					p.setInt(1 ,1);
					p.execute();
				}
			}
		}
		
		logger.info("Code warmup complete. Starting performance tests.");
		for(int i = 0; i < 5; i++) {
			logger.info("Using statement to insert data");
			basicInsert(args);
			logger.info("Using a PreparedStatement to insert data");
			insertPreparedStatementTest(args);
			logger.info("Using a PreparedStatement with IFXUSE_PUT");
			insertPreparedStatementWithIFXUSEPUTTest(args);
			logger.info("Using a PreparedStatement with batches to insert data");
			insertPreparedStatementBatchedTest(args);
			logger.info("Using a PreparedStatement with batches and IFX_USEPUT option to insert data");
			insertPreparedStatementBatchedUsePutTest(args);

		}
		
		printAverages();
	}
	
	private static void printAverages() {
		logger.info("Printing out averages for all tests...");
		logger.info(" The order of output is the expected performace start with the worst and ending with best.");
		logger.info(" However the last 2 can be somewhat close and may switch the order.");
		for(Map.Entry<String, List<Long>> entry : PERFORMANCE_MAP.entrySet()) {
			OptionalDouble average = entry.getValue()
					.stream()
					.mapToLong(a -> a)
					.average();
			logger.info("{}ms | {}r/s | {}", average.getAsDouble(),
					(Double.valueOf(INSERT_SIZE / (average.getAsDouble()) * 1000).intValue()), entry.getKey());
		}
	}
	
	private static void tableCheck(Connection c) throws SQLException {
		try(Statement s = c.createStatement()) {
			try(ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM simpleTable")) {
				rs.next();
				assert rs.getInt(1) == INSERT_SIZE;
			}
		}
	}
	
	private static void timePrint(long startTime, long stopTime, String name) {
		logger.info("Execution time : {} ms", stopTime - startTime);
		logger.info("Records per sec: {} r/s",
				Double.valueOf(INSERT_SIZE / (stopTime - startTime - 0.0) * 1000).intValue());
		PERFORMANCE_MAP.putIfAbsent(name, new ArrayList<>());
		PERFORMANCE_MAP.get(name).add(stopTime - startTime);
	}
	
	private static void basicInsert(String [] args) throws SQLException {
		try(Connection c = DriverManager.getConnection(args[0])) {
			Statement s = c.createStatement();
			s.execute("CREATE TEMP TABLE simpleTable (a integer, b varchar(255)) WITH NO LOG");
			
			startTime = System.currentTimeMillis();
			for (int i = 0; i < INSERT_SIZE; i++) {
				s.execute("INSERT INTO simpleTable VALUES(1, 'some value here. This is not dynamic but slower than prepared statement')");
			}
			stopTime = System.currentTimeMillis();
			timePrint(startTime, stopTime, "Statement Insert Performance");
			tableCheck(c);
		}
	}
	
	private static void insertPreparedStatementTest(String [] args) throws SQLException {
		try(Connection c = DriverManager.getConnection(args[0])) {
			Statement s = c.createStatement();
			s.execute("CREATE TEMP TABLE simpleTable (a integer, b varchar(255)) WITH NO LOG");
			PreparedStatement p = c.prepareStatement("INSERT INTO simpleTable VALUES(?, ?)");
			p.setInt(1, 1);
			p.setString(2, "some value here. This is not dynamic but slower than prepared statement");
			
			startTime = System.currentTimeMillis();
			for (int i = 0; i < INSERT_SIZE; i++) {
				p.execute();
			}
			stopTime = System.currentTimeMillis();
			timePrint(startTime, stopTime, "Using a PreparedStatement to insert data");
			tableCheck(c);
		}
	}
	
	private static void insertPreparedStatementWithIFXUSEPUTTest(String [] args) throws SQLException {
		Properties prop = new Properties();
		prop.setProperty("IFX_USEPUT", "1");
		try(Connection c = DriverManager.getConnection(args[0], prop)) {
			Statement s = c.createStatement();
			s.execute("CREATE TEMP TABLE simpleTable (a integer, b varchar(255)) WITH NO LOG");
			PreparedStatement p = c.prepareStatement("INSERT INTO simpleTable VALUES(?, ?)");
			p.setInt(1, 1);
			p.setString(2, "some value here. This is not dynamic but slower than prepared statement");
			
			startTime = System.currentTimeMillis();
			for (int i = 0; i < INSERT_SIZE; i++) {
				p.execute();
			}
			stopTime = System.currentTimeMillis();
			timePrint(startTime, stopTime, "Using a PreparedStatement [IFXUSE_PUT] to insert data");
			tableCheck(c);
		}
	}
	
	private static void insertPreparedStatementBatchedTest(String [] args) throws SQLException {
		try(Connection c = DriverManager.getConnection(args[0], null)) {
			Statement s = c.createStatement();
			s.execute("CREATE TEMP TABLE simpleTable (a integer, b varchar(255)) WITH NO LOG");
			PreparedStatement p = c.prepareStatement("INSERT INTO simpleTable VALUES(?, ?)");
			p.setInt(1, 1);
			p.setString(2, "some value here. This is not dynamic but slower than prepared statement");
			
			startTime = System.currentTimeMillis();
			for (int i = 0; i < INSERT_SIZE; i++) {
				p.addBatch();
			}
			p.executeBatch();
			stopTime = System.currentTimeMillis();
			timePrint(startTime, stopTime, "Using a PreparedStatement with batches to insert data");
			tableCheck(c);
		}
	}
	
	private static void insertPreparedStatementBatchedUsePutTest(String [] args) throws SQLException {
		Properties prop = new Properties();
		prop.setProperty("IFX_USEPUT", "1");
		try(Connection c = DriverManager.getConnection(args[0], prop)) {
			Statement s = c.createStatement();
			s.execute("CREATE TEMP TABLE simpleTable (a integer, b varchar(255)) WITH NO LOG");
			PreparedStatement p = c.prepareStatement("INSERT INTO simpleTable VALUES(?, ?)");
			p.setInt(1, 1);
			p.setString(2, "some value here. This is not dynamic but slower than prepared statement");
			
			startTime = System.currentTimeMillis();
			for (int i = 0; i < INSERT_SIZE; i++) {
				p.addBatch();
			}
			p.executeBatch();
			stopTime = System.currentTimeMillis();
			timePrint(startTime, stopTime, "Using a PreparedStatement with batches and IFX_USEPUT option to insert data");
			tableCheck(c);
		}
	}
}
