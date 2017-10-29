$(function () {
    var socketUrl = $("body").data("ws-url");
    var webSocket = null;

    function startWebSocket() {
        webSocket = new WebSocket(socketUrl);
        webSocket.onmessage = function (message) {
            var data = JSON.parse(message.data);
            $("#readings").removeClass("hidden");
            $("#no-data-msg").addClass("hidden");
            $("#timestamp-value").text(data.date);
            $("#temperature-value").text(data.temperature);
            $("#humidity-value").text(data.humidity);
        };
        webSocket.onopen = function () {
            $("#connection-error-msg").addClass("hidden");
            webSocket.send(JSON.stringify({'command': 'GET_LATEST_READINGS'}));
        };
        webSocket.onclose = function () {
            $("#connection-error-msg").removeClass("hidden");
        }
    }

    function checkWebSocket() {
        if (!webSocket || webSocket.readyState == WebSocket.CLOSED) {
            startWebSocket();
        }
    }

    startWebSocket();
    setInterval(checkWebSocket, 10000);
});
