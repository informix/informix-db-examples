# Informix Change Data Capture with Java

This example shows how to use the Informix CDC (change data capture) feature with Java.

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

## Running the Samples

Make sure you have `java` in your PATH.

You can run any example by starting the jar we have just built, and provide your connection URL. 

### Create the syscdcv1 database
All CDC programs must have the syscdcv1 database created first.

You do this on the Informix server by running the following command

```bash
[@informix] dbaccess - $INFORMIXDIR/etc/syscdcv1.sql
```

### Create a sample database/table
You can use your own, or you can use this quick exmaple

```bash
[@informix] dbaccess - -
> CREATE DATABASE testdb with log;
> CREATE table cdctable (a integer, b varchar(200));
```

### Run the program
**You MUST connect to the syscdcv1 database to use the CDC streaming**

`> java -cp build/libs/cdc.jar  "jdbc:informix-sqli://localhost:9088/syscdcv1:user=informix;password=password"`

If you decide to watch another database/table, simply change the line in the `src/main/java/cdc/App.java` file from `"testdb:informix:cdcTable", "a", "b"` to what you want to watch.  

The format is:

```
"database-name:user-name:tablename", "col1-name", "col2-name", ...."colN-name"
```

You can also change the program to watch multiple tables.

### Testing the CDC application

While this program is running, open a SQL session against the server and run some insert/update/delete statement against the table you are monitoring. Following the example defined above you can run

```bash
[@informix] dbaccess - -
> BEGIN WORK;
> insert into cdctable values(1, "Hello there");
> COMMIT WORK;
```
You should see messages appear in the running CDC application that it noticed the changes you made.