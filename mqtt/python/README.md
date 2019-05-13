# Informix MQTT Code Samples using Python

The MQTT/python example programs rely on the paho-mqtt module.  You will need to install this module for these sample programs to work.


Sample | Description
-------|------------
basicQuery | Example using the Python driver to build a basic application.


## Install python and paho-mqtt 

To install run **pip install paho-mqtt**

You will need to configure and run the Wire Listener to support the MQTT protocol.  The following is a sample properties that can be used to setup the Wire Listener to use MQTT.

``` code
 listener.port = 27883
 listener.idle.timeout = 60000
 listener.type = mqtt
 url=jdbc:informix-sqli/localhost:9189/sysmaster:USER=informix;PASSWORD=informix
```

    
