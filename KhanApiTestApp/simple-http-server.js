var http = require("http");

var response = "this is a response 2";

http.createServer(function (req, res) {
	res.writeHead(200, {"Content-Type" : "text/plain"});
	res.end(response);
}).listen(33333);