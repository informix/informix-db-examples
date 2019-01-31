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
package performance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * OPTOFC is a network traffic reduction feature.
 * 
 * It instructs the server to automatically close the cursor of a resultset after the
 * last rows of data are sent to the client. This reduces one round trip for every closing resultset 
 *
 */
public class OptOFC {

	private static final Logger logger = LoggerFactory.getLogger(OptOFC.class);
	
	public static void main(String[] args) throws SQLException {
		if(args.length != 1) {
			throw new NullPointerException("You must pass the connection URL as the first argument to the demo");
		}
		
		/*
		 * We can set the use of OPTOFC on the connection by setting the property
		 */
		Properties p = new Properties();
		p.setProperty("OPTOFC", "1");
		try(Connection con = DriverManager.getConnection(args[0], p)) {
			Statement s = con.createStatement();

			
			try(ResultSet rs = s.executeQuery("SELECT FIRST 10 tabname FROM systables")) {
				/*
				 * When the resultset is done fetching rows, the cursor is automatically closed by the server
				 */
				while(rs.next()) {
					logger.info("{}", rs.getString(1));
				}
				rs.close(); //No network traffic sent. Cursor is already closed on the server
			}
		}
		/*
		 * It is still/always good practice to close all resources regardless of the use of OPTOFC
		 * This allows you to turn it on/off w/o leaving resources open.
		 * It also allows the JDBC driver to ensure client side resources are freed up
		 */
	}
}
