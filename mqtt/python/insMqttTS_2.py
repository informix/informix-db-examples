"""  
     This code will insert into a timeseries table see iot_db.sql
     For the timestamp value it uses a datetime string year to fraction(5)
     Example: 2017-06-04 08:20:30.05467
"""
from datetime import datetime
import paho.mqtt.client as mqtt
import time
import json

NUMINS = 10

connectionJson = json.loads(open("../../connections.json").read())
print(connectionJson)

def on_publish(client, userdata, mid):
    """ on_publish:
    """
    print("MID Published: ", mid)
    if mid == NUMINS:
        client.disconnect()


client = mqtt.Client(protocol=mqtt.MQTTv31)
client.on_publish = on_publish

client.connect(connectionJson['host'], connectionJson['mqttPort'])

client.loop_start()


dbname=connectionJson['database']+'.iot_data_ts'
for i in range(1, NUMINS + 1):
    #currentTimeStr = datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%f")[:-1]

    currentTimeNow = time.time()
    currentTimeInt = int(currentTimeNow*1000)
    currentTimeStr = datetime.fromtimestamp(currentTimeNow).strftime("%Y-%m-%dT%H:%M:%S.%f")[:-1]

    msgstr = '{  "id":1,  "ts" : "%s",  "col1"  : "king bob", "col2": "kevin"  }'  % (currentTimeStr) 


    print("Publish ",msgstr)
    print("   TimeNow:",currentTimeNow)
    print("   TimeInt:",currentTimeInt)
    print(" * TimeStr:",currentTimeStr)
    (result, mid) = client.publish(dbname, msgstr, qos=1)
    if result != mqtt.MQTT_ERR_SUCCESS:
        print("Error Publish: ", i)

    if  (i % 1000) == 0:
        print("I: ", i)


client.loop_forever()
