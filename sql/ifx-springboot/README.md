# Using Informix with Spring Boot

This is a quick example showing how to create a simple SpringBoot

**Note** :  This is a working example, possibly not the best.  There might other best practices using Spring and
Hibernate that should be applied that is not shown here.
 
## Dependencies
 
This project uses Gradle as the build system
 
## Building and running the project

You can compile and execute the project with the following command

**IMPORTANT** You must supply the connection URL to your informix database server. The server must have a database. 
You can use the stores_demo database or create your own.
```bash
$> ./gradlew clean bootRun -Pargs="jdbc:informix-sqli://localhost:9088/stores_demo:USER=informix;PASSWORD=in4mix"
```

## Spring Boot with BSON
This example includes how to link a non-standard JDBC type such as Informix BSON using the underlying Hibernate
 library to configure how to interpret the type.
 
See the files `HibernateBasicBsonObject.java` and `SimpleEntity.java` for an example of this.