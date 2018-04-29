import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ConnectionUtil {
	public static Connection getConnection(String filePath) throws Exception {
		JsonObject json = new JsonParser().parse(new JsonReader(new FileReader(filePath))).getAsJsonObject();
		Properties p = new Properties();
		p.setProperty("USER", json.get("user").getAsString());
		p.setProperty("password", json.get("password").getAsString());
		String connectionString = "jdbc:informix-sqli://" + json.get("host").getAsString() 
				+ ":" + json.get("port").getAsString() + "/" + json.get("database").getAsString();
		System.out.println(connectionString);
		return DriverManager.getConnection(connectionString, p);
	}
}
