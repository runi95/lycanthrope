var lobbies = {};
var currentLobbyId;
var playerNumber;

var csrf_name = $('meta[name="csrf_name"]').attr('content');
var csrf_value = $('meta[name="csrf_value"]').attr('content');

function changeView(view) {
    $("#nest").html(view);
}

function handleActions(message) {
    switch (message.action) {
        case "populatelobbies":
            populateLobbies(message.content);
            break;
        case "createLobby":
            console.log("createLobby");
            populateNewLobby(message.content);
            break;
        case "loadlobby":
            loadLobby(message.content);
            break;
        case "disconnected":
            disconnectPlayer(message.content);
            break;
    }
}

function populateNewLobby(newLobby) {
    lobbies[newLobby.key.id] = newLobby.key;

    console.log($("meta[name=nickname]").attr("content") + " === " + newLobby.val);

    if ($("meta[name=nickname]").attr("content") === newLobby.val) {
        createLobbyPrivate(newLobby.key);
    } else {
        addLobbyToLobbyTable(newLobby.key);
    }
}

function disconnectPlayer(leavingPlayer) {
    var playerElement = document.getElementById(leavingPlayer.id);
    if (playerElement != null) {
        var text = document.createTextNode("Open Slot");
        playerElement.replaceChild(text, playerElement.childNodes[0]);
        playerElement.setAttribute("id", "");
        playerElement.setAttribute("class", "list-group-item list-group-item-empty");
    }
}

function populateLobbies(lobbies) {
    for (var i = 0; i < lobbies.length; i++) {
        addLobbyToLobbyTable(lobbies[i]);
    }
}

function addLobbyToLobbyTable(lobby) {
    var lobbyDiv = document.createElement("div");
    var name = document.createElement("h4");
    var hr = document.createElement("hr");
    var lobbySize = document.createElement("div");
    var btn = document.createElement("button");
    btn.classList.add("btn", "btn-primary");
    btn.setAttribute("onClick", "joinLobbyRequest(" + lobby.id + ")");

    name.appendChild(document.createTextNode(lobby.name));
    lobbySize.appendChild(document.createTextNode(lobby.currentPlayerSize + "/" + lobby.lobbyMaxSize));
    btn.appendChild(document.createTextNode("Join"));

    lobbySize.setAttribute("id", lobby.id + "s");

    lobbyDiv.appendChild(name);
    lobbyDiv.appendChild(hr);
    lobbyDiv.appendChild(lobbySize);
    lobbyDiv.appendChild(btn);

    $("#lobbytable").append(lobbyDiv);
}

function loadLobby(message) {
    lobbies[message.id] = lobbies;

    if (message.id === currentLobbyId) {
        if (message.started) {
            // TODO: This is definitely not the best way to find our playerNumber, it should be changed in the future!
            for (var i = 0; i < message.users.length; i++) {
                if ($("meta[name=nickname]").attr("content") === message.users[i].nickname) {
                    playerNumber = message.users[i].playerNumber;
                }
            }

            joinGame(message.id);
        } else {
            for (var i = 0; i < message.users.length; i++) {
                var playerElement = document.getElementById(message.users[i].id);

                if (playerElement === null) {
                    var elements = document.getElementsByClassName("list-group-item-empty");
                    var element;
                    if (elements.length > 0) {
                        element = elements[0];
                    } else {
                        element = elements;
                    }

                    var text = document.createTextNode(message.users[i].nickname);
                    element.replaceChild(text, element.childNodes[0]);
                    element.setAttribute("id", message.users[i].id);

                    if ($("meta[name=nickname]").attr("content") === message.users[i].nickname) {
                        element.setAttribute("class", "list-group-item list-group-item-success");
                    } else {
                        element.setAttribute("class", "list-group-item list-group-item-default");
                    }
                }
            }
        }
    }
}

function createLobbyPrivate(lobby) {
    $.ajax({
        url: "/lobby/" + lobby.id,
        type: "GET",
        success: function (result) {
            changeView(result);
            currentLobbyId = lobby.id;
            loadLobby(lobby);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function joinGame(id) {
    $.ajax({
        url: "/game/" + id,
        type: "GET",
        success: function (result) {
            changeView(result);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function loadLobbyView(id) {
    $.ajax({
        url: "/lobby/" + id,
        type: "GET",
        success: function (result) {
            changeView(result);
            currentLobbyId = id;
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function joinLobbyRequest(id) {
    $.ajax({
        url: "/lobby/" + id,
        type: "GET",
        success: function (result) {
            changeView(result);
            currentLobbyId = id;
            joinLobby(id);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function getCreateLobbyView() {
    $.ajax({
        url: "/createLobby",
        type: "GET",
        success: function (result) {
            changeView(result);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function createNewLobby(roles, playerSize) {
    createLobby({key: roles, val: playerSize});
}

function loadLobbies() {
    $.ajax({
        url: "/lobbies",
        type: "GET",
        success: function (result) {
            changeView(result);
        },
        error: function (error) {
            console.log(error);
        }
    });
}