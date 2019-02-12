var stompClient = null;
connect();

function setConnected(connected) {
    if(!$("#reconnect").hasClass("text-hide")) {
        $("#reconnect").addClass("text-hide");
    }

    if(connected) {
        if($("#connected").hasClass("text-hide")) {
                $("#connected").removeClass("text-hide");
        }

        if(!$("#disconnected").hasClass("text-hide")) {
            $("#disconnected").addClass("text-hide");
        }
    } else {
        if(!$("#connected").hasClass("text-hide")) {
            $("#connected").addClass("text-hide");
        }

        if($("#disconnected").hasClass("text-hide")) {
            $("#disconnected").removeClass("text-hide");
        }

    }
}

function setConnecting() {
    if($("#reconnect").hasClass("text-hide")) {
        $("#reconnect").removeClass("text-hide");
    }

    if(!$("#connected").hasClass("text-hide")) {
        $("#connected").addClass("text-hide");
    }

    if(!$("#disconnected").hasClass("text-hide")) {
        $("#disconnected").addClass("text-hide");
    }
}

function receiveMessage(message) {
    if (message.status != 200) {

        // Something went wrong!
        console.error(message.content);
    } else {
        handleActions(message);
    }
}

function connect() {
    setConnecting();

    var socket = new SockJS('/lycanthrope');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/endpoint/private', function (message) {
            receiveMessage(JSON.parse(message.body));
        });
        stompClient.subscribe('/endpoint/broadcast', function (message) {
            receiveMessage(JSON.parse(message.body));
        });

        stompClient.event
        getLobbies();
    }, function () {
        setConnected(false);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect(function () {
            console.log("Successfully disconnected the web socket.");
            setConnected(false);
        });
    }
}

function getLobbies() {
    stompClient.send("/websock/getLobbies", {}, null);
}

function joinLobby(lobbyId) {
    stompClient.send("/websock/joinLobby", {}, JSON.stringify({"action": "join", "value": lobbyId}));
}

function createLobby(data) {
    stompClient.send("/websock/createLobby", {}, JSON.stringify({"action": "create", "value": data}));
}

function nightAction(id) {
    stompClient.send("/websock/nightAction", {}, JSON.stringify({"action": "nightaction", "value": id}));
}

function voteAction(id) {
    stompClient.send("/websock/voteAction", {}, JSON.stringify({"action": "voteaction", "value": id}));
}