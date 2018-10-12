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
