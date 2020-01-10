# Performance

Performance in JDBC as in other client is both complicated and relative. As a client side driver, JDBC is as the mercy of both the Applications that run with it as well as the Server it is communicating with.

Given its pivotal role in providing the communication layer between the server and the application, it is important to know how you can minimize the impact of the driver in the performance of the entire solution.

## Network Performance

A number of options are available to help reduce the traffic and round trips needed over the network to access data. Examples such as AutoFree, OptOFC show how you can reduce the number of round trips if you are frequently closing Statement or ResultSet objects in your application.

## Insert Performance

There is a dedicated test InsertPerformance which attempts to show the various ways you can
insert a large amount of data into the server and what setup you might want to consider.

TL;DR: The output from that shows the averages at the end for a handful of runs for each type. It shows the total
 time executed, the average number of rows processed per second, and the type of insert used.

```shell script
[main] INFO performance.InsertPerformance - Printing out averages for all tests...
[main] INFO performance.InsertPerformance -  The order of output is the expected performace start with the worst and ending with best.
[main] INFO performance.InsertPerformance -  However the last 2 can be somewhat close and may switch the order.
[main] INFO performance.InsertPerformance - 17827.8ms | 560r/s | Statement Insert Performance
[main] INFO performance.InsertPerformance - 6373.8ms | 1568r/s | Using a PreparedStatement to insert data
[main] INFO performance.InsertPerformance - 6292.4ms | 1589r/s | Using a PreparedStatement [IFXUSE_PUT] to insert data
[main] INFO performance.InsertPerformance - 175.6ms | 56947r/s | Using a PreparedStatement with batches to insert data
[main] INFO performance.InsertPerformance - 135.4ms | 73855r/s | Using a PreparedStatement with batches and IFX_USEPUT option to insert data

```