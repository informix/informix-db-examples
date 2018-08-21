/*
 * Licensed Materials - Property of HCL
 * (c) Copyright HCL Technologies Ltd. 2018.  All Rights Reserved.
 */
package smartTriggers;

import java.sql.SQLException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.informix.smartTrigger.IfmxSmartTriggerCallback;
import com.informix.smartTrigger.IfxSmartTrigger;

public class SmartTrigger implements IfmxSmartTriggerCallback {
	
	private final JsonParser p = new JsonParser();
	public static void main(String[] args) throws SQLException {
		try(IfxSmartTrigger trigger = new IfxSmartTrigger(args[0]);) {
			trigger.timeout(5).label("bank_alert"); // optional parameters
			trigger.addTrigger("account", "informix", "bank", "SELECT * FROM account WHERE balance < 0", new SmartTrigger());
			trigger.watch();
		}
	}

	@Override
	public void notify(String jsonString) {
		JsonObject json = p.parse(jsonString).getAsJsonObject();
		System.out.println("Bank Account Ping!");
		if (json.has("ifx_isTimeout")) {
			System.out.println("-- No balance issues");
		} else {
			System.out.println("-- Bank Account Alert detected!");
			System.out.println("   " + json);
		}
	}
}
