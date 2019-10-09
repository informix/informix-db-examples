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
package cdc;

import com.informix.jdbcx.IfxDataSource;
import com.informix.stream.api.IfmxStreamRecord;
import com.informix.stream.cdc.IfxCDCEngine;
import com.informix.stream.cdc.records.IfxCDCOperationRecord;

public class App {
    public static void main(String[] args) throws Exception {
        String url = null;
		if (args.length > 0)
			url = args[0];
		else {
			throw new NullPointerException("You must pass the connection URL as the first argument to the demo");
		}
        IfxDataSource ds = new IfxDataSource(url);
        IfxCDCEngine.Builder builder = new IfxCDCEngine.Builder(ds);
        builder.watchTable("testdb:informix:cdcTable", "a", "b");
        builder.timeout(5);
        try(IfxCDCEngine engine = builder.build()) {
            engine.init();
            IfmxStreamRecord record = null;
            while((record = engine.getRecord()) != null) {
                System.out.println(record);
                if(record.hasOperationData()) {
                    System.out.println(((IfxCDCOperationRecord)record).getData());
                }
            }
        }
    }
}
