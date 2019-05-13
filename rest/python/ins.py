"""  Uses iot_db.sql to insert into a vti timeseries table
"""
from datetime import datetime
from datetime import timedelta
import paho.mqtt.client as mqtt
import requests

ct = datetime.now()
ct = ct-timedelta(seconds=60)
cts = ct.strftime("%Y-%m-%d %H:%M:%S.%f")[:-1]
query='''{"$sql": "insert into iot_data_v values(1000,'desc', '%s' , ('{''id'':5}'::json::bson) )"}''' % (cts)
#query='''{"$sql": "insert into iot_data_v values(1000,'desc', '2019-1-1 01:01:01.00000', ('{''id'':5}'::json::bson) )"}'''

print "query: ", query
reply = requests.get("http://server:27018/iot/system.sql?query="+query)

print reply
if reply.status_code == 200:
   print "1000 inserted"
else:
   print "1000 Not inserted"
   sys.exit()


NUMINS = 10

def on_publish(client, userdata, mid):
    """ on_publish:
    """
    print("MID Published: ", mid)
    if mid == NUMINS:
        client.disconnect()


client = mqtt.Client(protocol=mqtt.MQTTv31)
client.on_publish = on_publish

client.connect("server", 27883)

client.loop_start()

"""  Msgstr is a json string that matches the vti table schema
"""
ct = datetime.now()
cts = ct.strftime("%Y-%m-%d %H:%M:%S.%f")[:-1]
for i in range(1, NUMINS + 1):
    msgstr = '{  "id":%d, "desc":"description data",  "ts" : "%s",  "json_data" : { "col4": "king bob"}  }'  % (1000, cts)
    print ("publish: " , msgstr)

    (result, mid) = client.publish("iot.iot_data_v", msgstr, qos=1)
    if result != mqtt.MQTT_ERR_SUCCESS:
        print("Error Publish: ", i)

    if  (i % 1000) == 0:
         print("I: ", i)

    ct = ct+timedelta(seconds=60)
    cts = ct.strftime("%Y-%m-%d %H:%M:%S.%f")[:-1]


client.loop_forever()

                                                                                                                                                                                                                   1,1           Top

