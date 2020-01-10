# Informix JDBC Code Samples

Here you will find examples for using the Informix JDBC driver for Java to build applications that use the Informix Database engine.

## Required Software

* Java SDK (IBM, Oracle, RedHat)
* Access to Maven Central for Informix JDBC as well as a few support libraries


## Building the Samples
All Examples are built using the Gradle build system. After downloading/cloning this project to your local workstation, you can open a command prompt or Unix Bash shell. Make sure you are inside the `java` subdirectory where this README.md file resides.

Make sure `javac` is set in your PATH so the gradle build can use it to compile the java files.


__Unix/Windows with Bash shell__

`> ./gradlew clean jar`

__Windows command prompt__

`> gradlew.bat clean jar`

If you wish you can load the entire Gradle project into your favorite IDE and use that to build, execute, debug the sample applications.

You will find a compiled jar file in `build/libs/informix-examples.java.jar`

## Running the Samples

Make sure you have `java` in your PATH.

You can run any example by starting the jar we have just built, and provide 

`> java -cp build/libs/informix-examples-java.jar performance.AutoFree "jdbc:informix-sqli://localhost:9088/testdb:user=informix;password=password"`

Unless mentioned otherwise, you must provide the connection URL as the first and only argument to the sample you wish to run.

## Samples


### Performance

Sample | Command | Description
-------|---------|-------------
[Auto Free](src/main/java/performance/AutoFree.java) | `> java -cp build/libs/informix-examples-java.jar performance.AutoFree "{connection-url}"` | Reduce network round trip by auto-freeing statement
[Insert Performance](src/main/java/performance/InsertPerformance.java) | `> java -cp build/libs/informix-examples-java.jar performance.InsertPerformance "{connection-url}"` | Speed test using a variety of insert options
[OptOFC](src/main/java/performance/OptOFC.java) | `> java -cp build/libs/informix-examples-java.jar performance.OptOFC "{connection-url}"` | Reduce network round trip by auto-close resultset (optimize open-fetch-close)
[Statement Cache](src/main/java/performance/StatementCache.java) | `> java -cp build/libs/informix-examples-java.jar performance.StatementCache "{connection-url}"` | Speed up database performance and simplify code with statement caches
### Data Types

Sample | Command | Description
-------|---------|-------------
[BSON](src/main/java/dataTypes/jsonBson/BsonExample.java) | `> java -cp build/libs/informix-examples-java.jar dataTypes.jsonBson.BsonExample "{connection-url}"` | Demonstrate use of BSON object
[JSON](src/main/java/dataTypes/jsonBson/JsonExample.java) | `> java -cp build/libs/informix-examples-java.jar dataTypes.jsonBson.JsonExample "{connection-url}"` | Demonstrate use of JSON with Informix
[Timeseries](src/main/java/dataTypes/timeseries/TimeseriesExample.java) | `> java -cp build/libs/informix-examples-java.jar dataTypes.timeseries.TimeseriesExample "{connection-url}"` | Demonstrate use of timeseries data with Informix

### Smart Triggers
Sample | Command | Description
-------|---------|-------------
[Smart Triggers](src/main/java/smartTriggers/SmartTrigger.java) | _See [README.md](src/main/java/smartTriggers/README.md)_ | Use smart trigger to receive remote triggers from database insert/update/deletes

## API Documentation

* <http://www.javadoc.io/doc/com.ibm.informix/jdbc/4.50.3>
