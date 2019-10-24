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
package com.informix.springboot;

import com.informix.jdbcx.IfxDataSource;
import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Arrays;

@SpringBootApplication
public class IfxSpringBootExample {

	private static final Logger logger = LoggerFactory.getLogger(IfxSpringBootExample.class);

	private static String CONNECTION_URL = "jdbc:informix-sqli://localhost:9088/stores_demo:USER=informix;PASSWORD=in4mix";
	public static void main(String[] args) {
		if(args.length != 1) {
			throw new NullPointerException("You must pass the connection URL as the first argument to the demo");
		}
		CONNECTION_URL = args[0];
		System.out.println("Connection URL:" + CONNECTION_URL);
		SpringApplication.run(IfxSpringBootExample.class, args);
	}

	@Bean
	public CommandLineRunner quickTest(SimpleEntityRepository repository) {
		return (args) -> {
			
			//Inject a table we can use to insert and query with
			try (Connection c = getInformixDataSource().getConnection()) {
				try(Statement s = c.createStatement()) {
					s.execute("CREATE TABLE IF NOT EXISTS simple_entity(id bigserial, name varchar" +
							"(255), detail BSON)");
				}
				
				
			}
			//Build a basic bson to test with
			BasicBSONObject json = new BasicBSONObject("name", "I'm a valid BSON!");
			json.put("innerJson", new BasicBSONObject("yep", "I work"));
			json.put("arr!", Arrays.asList(1, 2, 3));
			json.put("pie", 3.14);

			//Make sure we delete all entries first
			repository.deleteAll();
			
			//Save the BSON
			repository.save(new SimpleEntity("Quick Test", json));
			
			//Query for all the bson documents
			repository.findAll().forEach(entity -> {
				logger.info("Found: " + entity.toString());
			});
		};
	}

	@Bean
	public DataSource getInformixDataSource() throws SQLException {
		return new IfxDataSource(CONNECTION_URL);
	}
}
