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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Shows how to send and receive JSON objects from the Informix server
 * 
 * JSON objects from the client side are just Strings, so it's a great help
 * to use a JSON library to help manipulate and process the JSON structures, then send
 * the String representation to the server to store.
 *
 */
public class JsonExample {

	private static final Logger logger = LoggerFactory.getLogger(JsonExample.class);

	public static void main(String [] args) throws SQLException {
		if(args.length != 1) {
			throw new NullPointerException("You must pass the connection URL as the first argument to the demo");
		}
		
		try(Connection con = DriverManager.getConnection(args[0])) {
			try(Statement s = con.createStatement()) {
				s.execute("DROP TABLE IF EXISTS jsonTest");
				s.execute("CREATE TABLE jsonTest(a json)");
			}
			
			/*
			 * To insert a json object, we send it's string representation to the server
			 */
			try(PreparedStatement p = con.prepareStatement("INSERT INTO jsonTest VALUES(?)")) {
				/*
				 * Use any json library you wish.  
				 * This example uses the Gson library
				 */
				JsonObject json = new JsonObject();
				json.addProperty("age", 50);
				json.addProperty("name", "Batman");
				
				/*
				 * Set the end result as a string.  Informix does not have matching "type" like it does for bson
				 * so client applications should send/receive String data
				 */
				p.setString(1, json.toString());
				p.execute();
			}
			
			try(PreparedStatement p = con.prepareStatement("SELECT a FROM jsonTest")) {
				try(ResultSet rs = p.executeQuery()) {
					while(rs.next()) {
						String jsonAsString = rs.getString(1);
						JsonElement o = new JsonParser().parse(jsonAsString);
						JsonObject json = o.getAsJsonObject();
						logger.info("Name={}", json.get("name").getAsString());
					}
				}
			}
		}
	}
}
