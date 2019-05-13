DATABASE SYSADMIN;

DROP DATABASE if exists iot ;
CREATE DATABASE iot with log;

DATABASE iot;


-- 
-- Cleanup: Drop any existing tables
--

DROP TABLE IF EXISTS iot_data_v;
DROP TABLE IF EXISTS iot_data_ts;

-- 
-- Cleanup: Drop any existing sequences
--

DROP SEQUENCE IF EXISTS iot_data_seq;

CREATE SEQUENCE iot_data_seq 
   INCREMENT BY 1 START WITH 1; 

--
-- Drop and create the row types which are required for the result
-- time series
--

drop row type if exists one_result_t restrict;
drop row type if exists two_result_t restrict;

create row type one_result_t (
        ts datetime year to fraction(5),
        value1 FLOAT);

create row type two_result_t (
        ts datetime year to fraction(5),
        value1 FLOAT,
        value2 FLOAT);

--
-- Drop and create the row types for the structured Informix time series
-- based on native Informix data types
--

drop row type if exists iot_data_t restrict;

create row type iot_data_t (
	ts datetime year to fraction(5),
 	json_data BSON);

--
-- Destroy and (re-)create the Informix time series containers
--

EXECUTE PROCEDURE TSContainerDestroy('iot_data_cont');

execute procedure TSContainerCreate ( 'iot_data_cont', 'rootdbs', 'iot_data_t', 2048, 2048);


--
-- Create the Informix time series base tables
--

create table iot_data_ts ( 
	id INT8, 
	desc VARCHAR(128),
	data TIMESERIES(iot_data_t), 
	PRIMARY KEY (id) ) 
lock mode row;

--
-- Create the virtual table (relational) views based on the time series
-- base tables. Those views doen't require any storage.
--

execute procedure TSCreateVirtualTab ( 
	'iot_data_v', 
	'iot_data_ts', 
	'origin(2017-07-01 00:00:00.00000),calendar(ts_1min),container(iot_data_cont),threshold(0),irregular',0,'data');


--
-- Create the table (relational)
--

create table tab1
(
   col1 integer,
   col2 integer,
   col3 char(50)
);

