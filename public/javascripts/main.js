$(function () {
    var socketUrl = $("body").data("ws-url");
    var webSocket = new WebSocket(socketUrl);
    webSocket.onmessage = function (message) {
        var data = JSON.parse(message.data);
        $("#readings").show();
        $("#no-data-msg").hide();
        $("#timestamp-value").text(data.timestamp);
        $("#temperature-value").text(data.temperature);
        $("#humidity-value").text(data.humidity);
    }
});
