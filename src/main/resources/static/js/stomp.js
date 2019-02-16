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

        requestLobbies();
    }, function () {
        setConnected(false);

        var connection = document.getElementById("connection");
        var loader = document.getElementById("loader");
        if (connection && loader) {
            var div = document.createElement("div");
            var span = document.createElement("span");
            var spanText = document.createElement("span");
            span.setAttribute("class", "icon fas fa-times");
            spanText.append(document.createTextNode(" Connection failed!"));

            div.append(span);
            div.append(spanText);
            div.setAttribute("id", "connection");
            div.setAttribute("class", "text-danger");

            connection.remove();
            loader.appendChild(div);
        }
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

function requestLobbies() {
    stompClient.send("/websock/requestLobbies", {}, null);
}

function getLobbies() {
    stompClient.send("/websock/getLobbies", {}, null);
}

function requestJoinLobby(lobbyId) {
    stompClient.subscribe('/endpoint/broadcast/' + lobbyId, function (message) {
        receiveMessage(JSON.parse(message.body));
    });
    stompClient.send("/websock/joinLobby", {}, JSON.stringify({"action": "join", "value": lobbyId}));
}

function createLobby(data) {
    stompClient.send("/websock/createLobby", {}, JSON.stringify({"action": "create", "value": data}));
}

function nightAction(id) {
    stompClient.send("/websock/nightAction", {}, JSON.stringify({"action": "nightaction", "value": id}));
}

function hunterKill(id) {
    stompClient.send("/websock/hunterKill", {}, JSON.stringify({"action": "hunterkill", "value": id}));
}

function voteAction(id) {
    stompClient.send("/websock/voteAction", {}, JSON.stringify({"action": "voteaction", "value": id}));
}

function requestGameResult(id) {
    stompClient.send("/websock/requestGameResult/" + id, {}, JSON.stringify({"action": "changeView", "value": ""}));
}

function requestVoteAction() {
    stompClient.send("/websock/requestVoteAction", {}, JSON.stringify({"action": "changeView", "value": ""}));
}

function requestGame() {
    stompClient.send("/websock/requestGame", {}, JSON.stringify({"action": "changeView", "value": ""}));
}

function requestNightAction() {
    stompClient.send("/websock/requestNightAction", {}, JSON.stringify({"action": "changeView", "value": ""}));
}

function requestLobby(id) {
    stompClient.send("/websock/requestLobby/" + id, {}, JSON.stringify({"action": "changeView", "value": ""}));
}

function requestRoleReveal() {
    stompClient.send("/websock/requestRoleReveal", {}, JSON.stringify({"action": "changeView", "value": ""}));
}

function requestCreateLobby() {
    stompClient.send("/websock/requestCreateLobby", {}, JSON.stringify({"action": "changeView", "value": ""}));
}