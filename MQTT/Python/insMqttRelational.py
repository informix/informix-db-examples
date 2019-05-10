""" Uses iot_db.sql To insert into a relational table
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


for i in range(1, NUMINS + 1):
    ct = datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")[:-1]
    msgstr = '{  "col1":%d, "col2" :%d, "col3":"%s"}'  % (i, i+1,"king bob")

    (result, mid) = client.publish("iot.tab1", msgstr, qos=1)
    if result != mqtt.MQTT_ERR_SUCCESS:
        print("Error Publish: ", i)

    if  (i % 1000) == 0:
        print("I: ", i)


client.loop_forever()
