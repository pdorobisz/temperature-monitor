var webSocket = new WebSocket('ws://localhost:9000/ws');
webSocket.onopen = function (event) {
    console.log("it works");
    webSocket.send("hello");
};
webSocket.onmessage = function(data) { console.log(data); }

console.log("started");