function changeView(view) {
    $("#nest").html(view);
}

function handleActions(message) {
    switch(message.action) {
        case "populateboardtable":
            populateBoardTable(message.content);
            break;
        case "loadlobby":
            loadLobby(message.content);
            break;
    }
}

function populateBoardTable(boards) {
    for(var i = 0; i < boards.length; i++) {
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

/*
function addPlayer(playerid, nickname, isowner) {
    if (!playerlist.hasOwnProperty(playerid)) {
        playerlist[playerid] = nickname;
        var player = document.createElement("li");
        var text = document.createTextNode(nickname);
        player.appendChild(text);
        player.setAttribute("id", playerid);

        if (isowner == "true") {
            owner = playerid;
            player.setAttribute("class", "list-group-item list-group-item-success");
        } else
            player.setAttribute("class", "list-group-item list-group-item-default");

        document.getElementById("plist").appendChild(player);
    }
}
*/

function loadLobby(message) {

}

function joinBoard(id) {
    $.ajax({
        url: '/boards/' + id,
        type: "GET",
        success: function(result) {
            changeView(result);
            joinLobby(id);
        },
        error: function(error) {
            console.log("Error: " + error);
        }
    });
}