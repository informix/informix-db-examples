var ifxDriver = require('ifxnjs');
var connectionString = require('./getConnection.js').getConnection();
console.log(connectionString);
var conn = ifxDriver.openSync(connectionString);
var rows = conn.querySync("SELECT FIRST 10 tabid, tabname from systables");
console.log(rows);
conn.closeSync();