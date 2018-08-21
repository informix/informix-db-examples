# Performance

Performance in JDBC as in other client is both complicated and relative. As a client side driver, JDBC is as the mercy of both the Applications that run with it as well as the Server it is communicating with.

Given its pivotal role in providing the communication layer between the server and the application, it is important to know how you can minimize the impact of the driver in the performance of the entire solution.

## Network Performance

A number of options are available to help reduce the traffic and round trips needed over the network to access data. Examples such as AutoFree, OptOFC show how you can reduce the number of round trips if you are frequently closing Statement or ResultSet objects in your application.