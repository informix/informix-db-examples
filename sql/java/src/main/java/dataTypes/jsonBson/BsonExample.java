/*
 * Licensed Materials - Property of HCL
 * (c) Copyright HCL Technologies Ltd. 2018.  All Rights Reserved.
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
package dataTypes.jsonBson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Map;

import com.informix.jdbc.IfxBSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BsonExample {


	
	private static final Logger logger = LoggerFactory.getLogger(BsonExample.class);

	private Connection conn = null;
	private IfxBSONObject bsonObject = null;

	public static void main(String[] args) {
		String url = null;
		if (args.length > 0)
			url = args[0];
		else {
			throw new NullPointerException("You must pass the connection URL as the first argument to the demo");
		}
		try {
			BsonExample demo = new BsonExample();
			demo.run(url);
		} catch (SQLException e) {
			logger.error("SQL Code: {}. Message: {}", e.getErrorCode(), e.getMessage(), e);
		}
	}

	public void run(String url) throws SQLException {
		try (Connection con = DriverManager.getConnection(url)) {
			this.conn = con;
			createTables();
			informixBsonObject();
			insertBson();
			insertBsonAsString();
			basicBsonQuery();
		}
	}

	private void createTables() throws SQLException {
		/*
		 * Create a table for us to use with a BSON column
		 */
		try (Statement s = this.conn.createStatement()) {
			String dropSQL = "DROP TABLE IF EXISTS bsontab";
			String createSQL = "CREATE TABLE bsontab(c1 BSON)";
			logger.info("Exec: {}",dropSQL);
			s.execute(dropSQL);
			logger.info("Exec: {}", createSQL);
			s.execute(createSQL);
		}
	}

	private void informixBsonObject() {
		IfxBSONObject bson = new IfxBSONObject();
		bson.put("id", 1);
		bson.put("name", "John Smith");
		/*
		 * You can convert the bson to a straight map object with toMap();
		 */
		Map<String, Object> map = bson.toMap();
		logger.info("ID={} \t Name={}", map.get("id"), map.get("name"));

		/*
		 * You can also get values directly from the IfxBSONObject
		 */
		logger.info("ID is {}", bson.get("id"));
	}

	private void insertBson() throws SQLException {

		IfxBSONObject bson = new IfxBSONObject();
		bson.put("id", 1);
		bson.put("name", "John Smith");
		/*
		 *  Inserting a BSON Object into the table
		 */
		try (PreparedStatement p = this.conn.prepareStatement("INSERT INTO bsontab (c1) VALUES (?)")) {
			p.setObject(1, bson);
			p.executeUpdate();
		}
	}

	private void insertBsonAsString() throws SQLException {
		/*
		 *  Inserting a BSON as a string. Make sure we cast any string to ::json::bson so
		 *  it converts correctly
		 */
		try (PreparedStatement p = this.conn.prepareStatement("INSERT INTO bsontab (c1) VALUES (?::json::bson)")) {
			p.setString(1, "{\"id\": 2, \"name\" : \"Ricky Bobby\"}");
			p.executeUpdate();
		}
	}

	private void basicBsonQuery() throws SQLException {
		/*
		 *  Query the database and get back BSON documents
		 */
		try (PreparedStatement p = this.conn.prepareStatement("SELECT c1 FROM bsontab")) {
			try (ResultSet rs = p.executeQuery()) {
				while (rs.next()) {
					Object o = rs.getObject("c1");
					logger.info("Data type from query: {}", o.getClass().getName());
					logger.info("String output of object ==> {}", o.toString());
				}
			}
			/*
			 *  Casting the object you get back to an IfxBSONObject allows you to dig into
			 *  the document
			 */
			try (ResultSet rs = p.executeQuery()) {
				while (rs.next()) {
					IfxBSONObject o = (IfxBSONObject) rs.getObject("c1");
					logger.info("ID={} \t Name={}", o.get("id"), o.get("name"));
				}
			}
		}
	}
}
