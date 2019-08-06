/*
 * Licensed Materials - Property of HCL
 * (c) Copyright HCL Technologies Ltd. 2019.  All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package dataTypes.timeseries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeseriesExample {
	private static final Logger logger = LoggerFactory.getLogger(TimeseriesExample.class);

	private Connection conn = null;

	public static void main(String[] args) {
		String url = null;
		if (args.length > 0)
			url = args[0];
		else {
			throw new NullPointerException("You must pass the connection URL as the first argument to the demo");
		}
		try {
			TimeseriesExample demo = new TimeseriesExample();
			demo.run(url);
		} catch (SQLException e) {
			logger.error("SQL Code: {}. Message: {}", e.getErrorCode(), e.getMessage(), e);
		}
	}
	
	public void run(String url) throws SQLException {
		try (Connection con = DriverManager.getConnection(url)) {
			this.conn = con;
			setupTimeseries();
			createTimeseriesEntry();
			insertTimeseriesData();
			queryTimeseriesData();
		}
	}

	/**
	 * Setup for timeseries by creating a ROW type that will store the timeseries data
	 * Create a table that has a column of type timeseries(your-row-type)
	 * Create a container that will hold timeseries data of your ROW type
	 */
	private void setupTimeseries() throws SQLException {
		try (Statement s = this.conn.createStatement()) {
			s.execute("DROP TABLE IF EXISTS tstab1");
			s.execute("DROP ROW TYPE IF EXISTS ts_basic_row RESTRICT");
			s.execute("CREATE ROW TYPE IF NOT EXISTS ts_basic_row(entry_time DATETIME YEAR TO FRACTION(5), value integer NOT NULL)");
			s.execute("CREATE TABLE IF NOT EXISTS tstab1( id integer, sensor timeseries(ts_basic_row))");
			try {
				//Auto registers timeseries if it does not exist (12.10 or higher versions of the server)
				s.execute("EXECUTE PROCEDURE TSContainerCreate ('test_container', 'rootdbs','ts_basic_row', 0, 0)");
			} catch(SQLException e) {
				if(e.getErrorCode() != -937) {
					throw e;
				}
			}
		}
	}

	/**
	 * Creates a timeseries by inserting a row into our table that has a timeseries column
	 * 
	 * In most cases each row in a relational table represents a sensor or device or source that sends timeseries data
	 * We put the overall id and metadata in this row, then update the timeseries column when more timeseries data comes in
	 */
	private void createTimeseriesEntry() throws SQLException {
		try(PreparedStatement p = conn.prepareStatement("INSERT INTO tstab1 VALUES(?, ?)")) {
			p.setInt(1, 1);
			p.setString(2, "origin(2018-01-01 12:00:00.00000), calendar(ts_1min), container(test_container), threshold(0), irregular, []");
			p.execute();    
		}
	}
	
	/**
	 * Inserts data into the timeseries for the single sensor/source we have established
	 */
	private void insertTimeseriesData() throws SQLException {
		Calendar cal = Calendar.getInstance();
		Random r = new Random();
		logger.info("Inserting 1000 elements into the timeseries...");
		try(PreparedStatement p = conn.prepareStatement("UPDATE tstab1 SET sensor = PutElem(sensor, ROW(?, ?)::ts_basic_row) WHERE id=?")) {
			for(int i = 0; i < 1000; i++) {
				p.setDate(1, new java.sql.Date(cal.getTimeInMillis()));
				//add a minute to the calendar
				cal.add(Calendar.MINUTE, 1);
				p.setInt(2, r.nextInt(10)); //your sensor/timeseries value  here we limit to 10 values so we can query something interesting 
				p.setInt(3, 1);  //The row in your base table (matching the id column)
				p.execute();
			}
		}
		logger.info("1000 elements inserted");
	}

	private void queryTimeseriesData() throws SQLException {
		try(PreparedStatement p = conn.prepareStatement("SELECT * FROM tstab1")) {
			try(ResultSet rs = p.executeQuery()) {
				while(rs.next()) {
					logger.info("Sensor ID: {}", rs.getObject(1));
				}
			}
		}
		try(PreparedStatement p = conn.prepareStatement("SELECT * FROM tstab1")) {
			try(ResultSet rs = p.executeQuery()) {
				while(rs.next()) {
					logger.info("Sensor ID: {}", rs.getObject(1));
				}
			}
		}
	}
}