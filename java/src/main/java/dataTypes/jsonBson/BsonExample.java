/*
 * Licensed Materials - Property of HCL
 * (c) Copyright HCL Technologies Ltd. 2018.  All Rights Reserved.
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

public class BsonExample {
	Connection conn = null;
	IfxBSONObject bsonObject = null;

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
			System.err.println(MessageFormat.format("SQL Code: {0}. Message: {1}", e.getErrorCode(), e.getMessage()));
		}
	}

	public void run(String url) throws SQLException {
		System.out.println("******************* DEMO STARTS *********************************");
		try (Connection con = DriverManager.getConnection(url)) {
			this.conn = con;
			createTables();
			informixBsonObject();
			insertBson();
			insertBsonAsString();
			basicBsonQuery();
		}

		System.out.println("******************* DEMO ENDS  *********************************");
	}

	private void createTables() throws SQLException {
		/*
		 * Create a table for us to use with a BSON column
		 */
		try (Statement s = this.conn.createStatement()) {
			String dropSQL = "DROP TABLE IF EXISTS bsontab";
			String createSQL = "CREATE TABLE bsontab(c1 BSON)";
			System.out.println(dropSQL);
			s.execute(dropSQL);
			System.out.println(createSQL);
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
		System.out.println(MessageFormat.format("ID={0} \t Name={1}", map.get("id"), map.get("name")));

		/*
		 * You can also get values directly from the IfxBSONObject
		 */
		System.out.println(MessageFormat.format("ID is {0}", bson.get("id")));
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
					System.out.println(MessageFormat.format("Data type from query: {0}", o.getClass().getName()));
					System.out.println(MessageFormat.format("String output of object ==> {0}", o.toString()));
				}
			}
			/*
			 *  Casting the object you get back to an IfxBSONObject allows you to dig into
			 *  the document
			 */
			try (ResultSet rs = p.executeQuery()) {
				while (rs.next()) {
					IfxBSONObject o = (IfxBSONObject) rs.getObject("c1");
					System.out.println(MessageFormat.format("ID={0} \t Name={1}", o.get("id"), o.get("name")));
				}
			}
		}
	}
}
