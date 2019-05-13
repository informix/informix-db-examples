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
package smartTriggers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.informix.smartTrigger.IfmxSmartTriggerCallback;
import com.informix.smartTrigger.IfxSmartTrigger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SmartTrigger implements IfmxSmartTriggerCallback {
	
	private final JsonParser p = new JsonParser();
	private static final Logger logger = LoggerFactory.getLogger(SmartTrigger.class);

	public static void main(String[] args) throws SQLException, InterruptedException {
		try(Connection c = DriverManager.getConnection(args[0])) {
			try(Statement s = c.createStatement()) {
				s.execute("CREATE DATABASE IF NOT EXISTS banktest with log");
				s.execute("DATABASE banktest");
				s.execute("CREATE TABLE IF NOT EXISTS account (id integer primary key, name varchar(200), balance integer)");
				s.execute("DELETE FROM account");

				//Start with $20
				s.execute("INSERT INTO account values(1, 'Checking', 20)");
			}
		}
		try(IfxSmartTrigger trigger = new IfxSmartTrigger(args[0]);) {
			trigger.timeout(5).label("bank_alert"); // optional parameters
			trigger.addTrigger("account", "informix", "banktest", "SELECT * FROM account WHERE balance < 0", new SmartTrigger());
			trigger.start();

			Thread.sleep(5000);
			logger.info("Starting account updates");
			try(Connection c = DriverManager.getConnection(args[0])) {
				try(Statement s = c.createStatement()) {
					s.execute("DATABASE banktest");
				}
				try(PreparedStatement p = c.prepareStatement("UPDATE account set balance = ? WHERE id = 1")) {
					for(int i = 20; i > -20; i=i - 5) {
						p.setInt(1, i);
						p.execute();
						logger.info("Updated balance in table to ${}", i);
						Thread.sleep(2000);
					}
				}
			}
		}
	}

	@Override
	public void notify(String jsonString) {
		JsonObject json = p.parse(jsonString).getAsJsonObject();
		if (json.has("ifx_isTimeout")) {
			logger.debug("[SmartTrigger] Server ping: No balance issues");
		} else {
			json = json.get("rowdata").getAsJsonObject();
			logger.debug("{}", json);
			logger.warn("[SmartTrigger] ALERT on account #{}. Balance ${}", 
				json.get("id").getAsInt(), 
				json.get("balance").getAsInt());
			
		}
	}
}
