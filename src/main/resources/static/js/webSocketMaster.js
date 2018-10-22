var lobbies = {};
var currentLobbyId;
var playerNumber;

function changeView(view) {
    $("#nest").html(view);
}

function handleActions(message) {
    switch (message.action) {
        case "populateboardtable":
            populateBoardTable(message.content);
            break;
        case "loadlobby":
            loadLobby(message.content);
            break;
        case "diceresult":
            diceResult(message.content);
            break;
    }
}

function diceResult(diceThrow) {
    if (diceThrow.lastPlayerId == playerNumber) {
        $("#diceimg").attr("src", "/img/Dice_" + diceThrow.diceThrowResult + ".png");
    }

    if (diceThrow.gameEnded) {
        $("#ul" + diceThrow.winningPlayerId).append(document.createTextNode(" (winner)"));
    } else {
        if (diceThrow.nextPlayerId == playerNumber) {
            if($("#diceButton").hasClass("disabled")) {
                $("#diceButton").removeClass("disabled");
                $("#diceButton").prop('disabled', false);
            }
        }
    }

    $("#u" + diceThrow.lastPlayerId).remove();
    var div = document.createElement("div");
    div.setAttribute("id", "u" + diceThrow.lastPlayerId);
    div.setAttribute("class", "player player-" + diceThrow.lastPlayerId);

    $("#p" + diceThrow.diceThrowLandingSquare).append(div);
}

function populateBoardTable(boards) {
    for (var i = 0; i < boards.length; i++) {
        var board = document.createElement("div");
        var name = document.createElement("h4");
        var hr = document.createElement("hr");
        var size = document.createElement("div");
        var description = document.createElement("div");
        var btn = document.createElement("button");
        btn.classList.add("btn", "btn-primary");
        btn.setAttribute("onClick", "joinBoard(" + boards[i].id + ")");

        name.appendChild(document.createTextNode(boards[i].name));
        size.appendChild(document.createTextNode(boards[i].size));
        description.appendChild(document.createTextNode(boards[i].description));
        btn.appendChild(document.createTextNode("Join"));

        board.appendChild(name);
        board.appendChild(hr);
        board.appendChild(size);
        board.appendChild(description);
        board.appendChild(btn);

        $("#boardtable").append(board);
    }
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

function joinGame(id) {
    $.ajax({
        url: '/game/' + id,
        type: "GET",
        success: function (result) {
            changeView(result);
        },
        error: function (error) {
            console.log("Error: " + error);
        }
    });
}

function joinBoard(id) {
    $.ajax({
        url: '/boards/' + id,
        type: "GET",
        success: function (result) {
            changeView(result);
            currentLobbyId = id;
            joinLobby(id);
        },
        error: function (error) {
            console.log("Error: " + error);
        }
    });
}

function rollDiceAction() {
    if(!$("#diceButton").hasClass("disabled")) {
        $("#diceButton").addClass("disabled");
        $("#diceButton").prop('disabled', true);
    }

    rollDice();
}