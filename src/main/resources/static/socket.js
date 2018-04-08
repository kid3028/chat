var socket;
if (!window.WebSocket) {
    window.WebSocket = window.MozWebSocket;
}
if (window.WebSocket) {
    socket = new WebSocket("ws://localhost:8080/ws");
    socket.onmessage = function(event) {
        var datas = event.data.split(",");
        console.log("服务器消息====" + datas);
        $("#chat-box").text(datas);
    };
    socket.onopen = function(event) {
        $("#chat-box").text("connection has finished!");
    };
    socket.onclose = function(event) {
        $("#char-box").text("connection has closed!");
    };
} else {
    alert("你的浏览器不支持 WebSocket！");
}

function send() {
    if (!window.WebSocket) {
        return;
    }
    if (socket.readyState == WebSocket.OPEN) {
        console.log($("#chat-input").val());
        var data = $("#myid").val() + "," + $("#friendid").val() + "," + $("#chat-input").val();
        socket.send(data);
    } else {
        alert("连接没有开启.");
    }
}


function login() {

    if (!window.WebSocket) {
        return;
    }
    if (socket.readyState == WebSocket.OPEN) {
        var data = $("#myid").val();
        alert(data);
        socket.send(data);
    } else {
        alert("连接没有开启.");
    }

}



//
//var socket;
//if (!window.WebSocket) {
//    window.WebSocket = window.MozWebSocket;
//}
//if (window.WebSocket) {
//    socket = new WebSocket("ws://localhost:8080/ws");
//    //监听netty服务器消息并打印到页面上
//    socket.onmessage = function (event) {
//        var datas = event.data.split(",");
//        console.log("服务器消息====" + datas);
//        $("#chat-box").text(datas);
//    }
//
//    socket, onopen = function (event) {
//        $("#chat-box").text("connection has finished!")
//    }
//
//    socket.onclose = function (event) {
//        $("#char-box").text("connection has closed!")
//    }
//
//} else {
//    alert("你的浏览器不支持 WebSocket！");
//}
//
////将发送人接收人的id和要发生的消息发送出去
//function send() {
//    if (!window.WebSocket) {
//        return;
//    }
//    if (socket.readyState == WebSocket.OPEN) {
//        console.log($("#chat-input").val());
//        var data = $("#myid").val() + "," + $("#friendid").val() + "," + $("#chat-input").val();
//        socket.send(data);
//    } else {
//        alert("connecting...")
//    }
//
//
//}
////登录事件
//function login() {
//    var data = $("#myid").val();
//    alert(data);
//
//    //socket.send(data);
//}