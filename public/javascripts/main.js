var webSocket = new WebSocket('ws://localhost:9000/ws');
webSocket.onopen = function (event) {
    console.log("it works");
    var command = {'command': 'GET_LATEST_MEASUREMENT'}
    webSocket.send(JSON.stringify(command));
    // webSocket.send('aaa');
};
webSocket.onmessage = function(data) { console.log(data); }

console.log("started");