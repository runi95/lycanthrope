function changeView(view) {
    $("#nest").html(view);
}

function handleActions(message) {
    switch(message.action) {
        case "populateboardtable":
            populateBoardTable(message.content);
            break;
    }
}

function populateBoardTable(boards) {
    console.log(boards);

    for(var i = 0; i < boards.length; i++) {
        var board = document.createElement("div");
        var name = document.createElement("h4");
        var hr = document.createElement("hr");
        var size = document.createElement("div");
        var description = document.createElement("div");
        var button = document.createElement("button");
        button.classList.add("btn", "btn-primary");

        name.appendChild(document.createTextNode(boards[i].name));
        size.appendChild(document.createTextNode(boards[i].size));
        description.appendChild(document.createTextNode(boards[i].description));
        button.appendChild(document.createTextNode("Join"));

        board.appendChild(name);
        board.appendChild(hr);
        board.appendChild(size);
        board.appendChild(description);
        board.appendChild(button);

        $("#boardtable").append(board);
    }
}