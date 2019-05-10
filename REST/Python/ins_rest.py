import requests
import json


#query='''{"$sql": "insert into iot_data_v values(1000,'desc', '2018-11-22 01:00:02.00000', ('{''id'':5}'::json::bson) )"}'''
#query='''{"$sql": "insert into iot_data_v values(1000,'desc', '2018-11-22 01:00:02.00000', ('{id:5}'::json::bson) )"}'''
query='''{"$sql": "insert into tab1 values(1,2,'desc')"}'''

reply = requests.get("http://server:27018/iot/system.sql?query="+query)

if reply.status_code == 200:
   print "Data inserted"
else:
   print "Not inserted"
print reply
