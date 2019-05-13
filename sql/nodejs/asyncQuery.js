var ifxdriver = require('ifxnjs');
var connectionString = require('./getConnection.js').getConnection();
ifxdriver.open(connectionString, (err, conn) => {
  //Execute a query asynchronously
  if (err) {
    console.log(err);
  }
  conn.query("SELECT FIRST 10 tabid, tabname from systables", (err1, rows) => {
    if (err1) {
      console.log(err1);
    }
    console.log(rows);
    conn.close(() => {
      console.log("Connection closed");
    });
  });
});