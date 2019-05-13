""" Uses iot_db.sql To insert into a relational table
"""

from datetime import datetime
import paho.mqtt.client as mqtt
import json

connectionJson = json.loads(open("../../connections.json").read())
print(connectionJson)

NUMINS = 10 

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

dbname=connectionJson['database']+'.tab1'
for i in range(1, NUMINS + 1):
    ct = datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")[:-1]
    msgstr = '{  "col1":%d, "col2" :%d, "col3":"%s"}'  % (i, i+1,"king bob")


    (result, mid) = client.publish(dbname, msgstr, qos=1)
    if result != mqtt.MQTT_ERR_SUCCESS:
        print("Error Publish: ", i)

    if  (i % 1000) == 0:
        print("I: ", i)


client.loop_forever()
