import requests
import json

connectionJson = json.loads(open("../../connections.json").read())
print(connectionJson)

#client.connect(connectionJson['host'], connectionJson['restPort'])

query='''{"$sql": "execute function admin ('create dbspace', 'dbs1', '/opt/ibm/data/spaces/dbs1', '20M', '0')"  }'''

req = "http://"+connectionJson['host']+":"+connectionJson['restPort']+"/sysadmin/system.sql?query="+query

reply = requests.get(req)
if reply.status_code == 200:
   print "Query SUCCESS"
else:
   print "Query FAILURE"

print reply.content
