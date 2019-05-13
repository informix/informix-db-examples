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
package setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Properties;

import com.informix.jdbc.IfmxStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Setup {

  private static final Logger logger = LoggerFactory.getLogger(Setup.class);

  public static void main(String [] args) throws SQLException {
		if(args.length != 1) {
			throw new NullPointerException("You must pass the connection URL as the first argument to the demo");
    }
    try(Connection con = DriverManager.getConnection(args[0])) {
      try(Statement s = con.createStatement()) {
        s.execute("DATABASE sysmaster");
        s.execute("DROP DATABASE IF EXISTS banktest");
        s.execute("CREATE DATABASE banktest WITH LOG");
      }
    }
    logger.info("Setup complete");
  }
}