from datetime import datetime
import paho.mqtt.client as mqtt
imiport json

###  Open the Connection parameters file
###

connectionJson = json.loads(open("../../connections.json").read())
print(connectionJson)


NUMINS = 10 

def on_publish(client, userdata, mid):
    ### on_publish
    ### When an Item is published, this method is called
    print("MID Published: ", mid)
    if mid == NUMINS:
        client.disconnect()


client = mqtt.Client(protocol=mqtt.MQTTv31)
client.on_publish = on_publish

### Connect to Informix MQTT WL
client.connect(connectionJson['host'], connectionJson['mqttPort'])

client.loop_start()

### Setup data for insert
### Publish data to table name collection1
dbname=connectionJson['database']+'.collection1'
for i in range(1, NUMINS + 1):
    ct = datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")[:-1]
    msgstr = '{  "sensor_id":%d, "tstamp" : "%s",  "d" : { "col4": "king bob"}  }'  % (i, ct)

    (result, mid) = client.publish(dbname, msgstr, qos=1)
    if result != mqtt.MQTT_ERR_SUCCESS:
        print("Error Publish: ", i)

    if  (i % 1000) == 0:
        print("I: ", i)


client.loop_forever()
