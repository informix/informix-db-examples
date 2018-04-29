import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Shows the basic Java code required to execute a query and retrieve the results
 * @author BRIAN.HUGHES
 */
public class Queries {

	public static void main(String[] args) throws Exception {
		/*
		 * Use a common utility to get a connection. Wrapped in a try block this will auto-close
		 * the connection on either success or failure of the code inside
		 */
		try (Connection con = ConnectionUtil.getConnection("../../../connections.json")) {
			// Create a statement object to execute SQL statements
			// This can be reused.
			Statement stmt = con.createStatement();
			
			// Execute a query returns a ResultSet which lets you iterate through
			// the rows of the database.
			ResultSet rs = stmt.executeQuery("SELECT FIRST 10 tabid, tabname from systables");
			while(rs.next()) {
				// Use the ResultSet API to get Integer, Date, String, Long, and other data types from
				// the table.
				System.out.print("Table ID: " + rs.getInt(1));
				System.out.println("\tTable Name: " + rs.getString(2));
			}
			// Always remember to close your resultset and Statement objects when you are
			// finished with them.
			rs.close();
			stmt.close();
		}	// The Connection will be closed beyond this point.
	}

}
