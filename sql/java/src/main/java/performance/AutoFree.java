/*
 * Licensed Materials - Property of HCL
 * (c) Copyright HCL Technologies Ltd. 2018, 2019.  All Rights Reserved.
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
package performance;

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

/**
 * 
 * AutoFree is a feature that can save a round trip when closing both a ResultSet and a Statement object
 * It does this by instructing the server to automatically free the statement when the ResultSet Cursor is closed.
 * 
 */
public class AutoFree {

	private static final Logger logger = LoggerFactory.getLogger(AutoFree.class);

	public static void main(String[] args) throws SQLException {
		if(args.length != 1) {
			throw new NullPointerException("You must pass the connection URL as the first argument to the demo");
		}
		
		/*
		 * We can set the use of AUTOFREE on the connection by setting the property
		 */
		Properties p = new Properties();
		p.setProperty("IFX_AUTOFREE", "1");
		try(Connection con = DriverManager.getConnection(args[0], p)) {
			Statement s = con.createStatement();
			
			/*
			 * You can also set autofree on/off for individual statements
			 */
			((IfmxStatement)s).setAutoFree(true);
			
			try(ResultSet rs = s.executeQuery("SELECT FIRST 10 tabname FROM systables")) {
				while(rs.next()) {
					logger.info("{}", rs.getString(1));
				}
			}
			/*
			 * When the result set above closes, the statement object itself is automatically released on the server.
			 */
			s.close(); //No network traffic sent. Cursor is already freed on the server
		}
		/*
		 * It is still/always good practice to close all resources regardless of the use of AUTOFREE
		 * This allows you to turn it on/off w/o leaving resources open.
		 * It also allows the JDBC driver to ensure client side resources are freed up
		 */
	}

}
