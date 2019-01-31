const fs = require('fs');

module.exports.getConnection = function() {
    var contents = fs.readFileSync('../connections.json', 'utf8');
    var jsonContents = JSON.parse(contents);
    var connectionString = "SERVER=" + jsonContents["server"] + ";DATABASE=" + jsonContents["database"]
        + ";HOST=" + jsonContents["host"] + ";SERVICE=" + jsonContents["port"] + ";UID=" + jsonContents["user"] + ";PWD=" + jsonContents["password"] + ";PROTOCOL=onsoctcp";
    console.log(connectionString);
    return connectionString;
}