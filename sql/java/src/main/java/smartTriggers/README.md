# Informix Smart Trigger Demo

## Requirements

* Informix 12.10xC10 or higher
* Informix JDBC Driver 4.10.JC10 or higher

## What is it?

Informix Smart Triggers is a mechanism to watch for certain events on your database, then initiate a triggered callback in your Java application

## Supporting Documentation

* <http://www.informixcommunity.com/blogs/introducing-informix-smart-triggers>
* <http://informix.hcldoc.com/help/index.jsp?topic=%2Fcom.ibm.erep.doc%2Fids_erp_pd_01.htm>
* <http://informix.hcldoc.com/help/index.jsp?topic=%2Fcom.ibm.jdbc_pg.doc%2Fids_jdbc_st_01.htm>
* <http://static.javadoc.io/com.ibm.informix/jdbc/4.10.12/com/informix/smartTrigger/package-summary.html>

## Setup

Smart Triggers relies on Informix Enterprise Replication to function.  If you don't already have Enterprise Replication enabled on your system, it will be auto-enabled as long as you have a storage pool available.

```
database sysadmin;
execute function task( 'storagepool add', '/informix/storage', '0', '0', '20000', '1' );
```

With the setup ready, you can create an IfxSmartTrigger object.  This object needs a URL or datasource to connect with and that connection has a couple requirements

* The connection must be to the `sysadmin` database 
* The user must have replication permission `"execute function task('grant admin', ‘myuser’, 'replication');"`
* The user must have permission to read the table(s) you want a trigger on

## Running the demo

* You must pass in the JDBC URL as the first argument to the program.
* `> java -cp build/libs/informix-examples-java.jar smartTriggers.SmartTrigger "{connection-url}"`
