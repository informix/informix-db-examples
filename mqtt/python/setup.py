import IfxPy
import json
import datetime

connectionJson = json.loads(open("../../connections.json").read());
print (connectionJson)

connectionString = "SERVER=" + connectionJson['server'] + ";DATABASE=" + connectionJson['database'] + ";HOST=" + connectionJson['host'] + ";SERVICE=" + str(connectionJson['port']) + ";UID=" + connectionJson['user'] + ";PWD=" + connectionJson['password'] + ";PROTOCOL=onsoctcp"
print (connectionString)
conn = IfxPy.connect(connectionString, "", "")

dt=datetime.datetime.now()
ifx_dt=dt.strftime("%Y-%m-%d %H:%M:%S.%f")[:-1]
print (dt)
print (ifx_dt)

SetupSqlSet = [
    "DROP TABLE IF EXISTS iot_data_v",
    "DROP TABLE IF EXISTS iot_data_ts",
    "DROP SEQUENCE IF EXISTS iot_data_seq",
    "CREATE SEQUENCE iot_data_seq increment by 1 start with 1",
    "DROP ROW TYPE IF EXISTS one_result_t restrict",
    "DROP ROW TYPE IF EXISTS two_result_t restrict",
    "CREATE ROW TYPE one_result_t ( ts datetime year to fraction(5), value1 FLOAT)",
    "CREATE ROW TYPE two_result_t ( ts datetime year to fraction(5), value1 FLOAT, value2 FLOAT)",
    "DROP ROW TYPE IF EXISTS iot_data_t restrict",
    "CREATE ROW TYPE iot_data_t( ts datetime year to fraction(5), json_data BSON)",
    "EXECUTE PROCEDURE TSContainerDestroy('iot_data_cont')", 
    "EXECUTE PROCEDURE TSContainerCreate('iot_data_cont', 'rootdbs', 'iot_data_t', 2048, 2048)", 
    "CREATE TABLE iot_data_ts ( id INT8, desc VARDCHAR(128), data TIMESERIES(iot_data_t), PRIMARY KEY(id)) lock mode row", 
    "EXECUTE PROCEDURE TSCreateVirtualTab('iot_data_v', 'iot_data_ts', 'origin("+ifx_dt+"), calendar(ts_1min), container(iot_data_cont), threshold(0), irregular', 0, 'data')", 
    "DROP TABLE IF EXISTS tab1 ",
    "CREATE TABLE tab1 (col1 integer, col2 integer, col3 char(50))"
]
	
for sql in SetupSqlSet:
    print (sql)
    stmt = IfxPy.exec_immediate(conn, sql)