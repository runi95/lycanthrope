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
        case "changeView":
            changeView(message.content);
            break;
        case "populatelobbies":
            populateLobbies(message.content);
            break;
        case "createLobby":
            console.log("createLobby");
            populateNewLobby(message.content);
            break;
        case "updateVotes":
            updateVotes(message.content);
            break;
        case "loadlobby":
            loadLobby(message.content);
            break;
        case "disconnected":
            disconnectPlayer(message.content);
            break;
        case "requestNightAction":
            getNightAction();
            break;
        case "requestGame":
            getGame();
            break;
        case "requestVoteAction":
            getVoteAction();
            break;
        case "requestGameEndAction":
            getGameResult(message.content);
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
        if (message.state >= 2) {
            // TODO: This is definitely not the best way to find our playerNumber, it should be changed in the future!
            for (var i = 0; i < message.users.length; i++) {
                if ($("meta[name=nickname]").attr("content") === message.users[i].nickname) {
                    playerNumber = message.users[i].playerNumber;
                }
            }

            joinGame(message);
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

function updateVotes(vote) {
    if ($("meta[name=nickname]").attr("content") === vote.voter) {
        if (vote.previousVote) {
            var previousVotedPlayer = document.getElementById(vote.previousVote);
            if (previousVotedPlayer) {
                previousVotedPlayer.setAttribute("class", "btn btn-block btn-primary");
            }
        }

        if (vote.voteIndicator === "+") {
            var votedPlayer = document.getElementById(vote.votedFor);
            if (votedPlayer) {
                votedPlayer.setAttribute("class", "btn btn-block btn-outline-secondary");
            } else {
                console.warn("Could not find votedPlayer(", votedPlayer, ")");
            }
        }
    }

    if (vote.previousVote) {
        var previousVoteCount = document.getElementById("b" + vote.previousVote);
        if (previousVoteCount) {
            previousVoteCount.replaceChild(document.createTextNode(vote.previousVotes), previousVoteCount.childNodes[0]);
        }
    }

    var votedPlayerCount = document.getElementById("b" + vote.votedFor);
    if (votedPlayerCount) {
        votedPlayerCount.replaceChild(document.createTextNode(vote.votes), votedPlayerCount.childNodes[0]);
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

function joinGame(lobby) {
    var url;
    if (lobby.state === 2) {
        url = "/game/" + lobby.id + "/roleReveal";
    } else {
        url = "/game/" + lobby.id;
    }

    $.ajax({
        url: url,
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

function getNightAction() {
    $.ajax({
        url: "/nightAction",
        type: "GET",
        success: function (result) {
            changeView(result);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function getGame() {
    $.ajax({
        url: "/game",
        type: "GET",
        success: function (result) {
            changeView(result);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function getVoteAction() {
    $.ajax({
        url: "/voteAction",
        type: "GET",
        success: function (result) {
            changeView(result);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function getGameResult(gameResultId) {
    $.ajax({
        url: "/result/" + gameResultId,
        type: "GET",
        success: function (result) {
            changeView(result);
        },
        error: function (error) {
            console.log(error);
        }
    });
}