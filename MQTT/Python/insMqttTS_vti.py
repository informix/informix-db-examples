"""  Uses iot_db.sql to insert into a vti timeseries table
"""
from datetime import datetime
import paho.mqtt.client as mqtt


NUMINS = 10 

def on_publish(client, userdata, mid):
    """ on_publish:
    """
    print("MID Published: ", mid)
    if mid == NUMINS:
        client.disconnect()


client = mqtt.Client(protocol=mqtt.MQTTv31)
client.on_publish = on_publish

client.connect("127.0.0.1", 27883)

client.loop_start()

"""  Msgstr is a json string that matches the vti table schema
"""
for i in range(1, NUMINS + 1):
    ct = datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")[:-1]
    msgstr = '{  "id":%d, "desc":"description data",  "ts" : "%s",  "json_data" : { "col4": "king bob"}  }'  % (i, ct)

    (result, mid) = client.publish("iot.iot_data_v", msgstr, qos=1)
    if result != mqtt.MQTT_ERR_SUCCESS:
        print("Error Publish: ", i)

    if  (i % 1000) == 0:
        print("I: ", i)


client.loop_forever()
