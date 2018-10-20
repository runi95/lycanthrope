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

function joinBoard(id) {
    console.log("You clicked on: " + id);

    $.ajax({
        url: '/boards/' + id,
        type: "GET",
        success: function(result) {
            changeView(result);
        },
        error: function(error) {
            console.log("Error: " + error);
        }
    });
}