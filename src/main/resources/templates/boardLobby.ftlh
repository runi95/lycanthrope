<div class="container">
    <div class="row">
        <div class="col-8 offset-2">
            <div class="text-center">
                <h1>
                    You are now in ${board.name}
                </h1>
            </div>
        </div>
    </div>
    <hr>
    <div class="row">
        <div class="col-6 offset-2">
            <#if board??>
                <div class="text-center">
                    <small>Players in lobby</small>
                    <ul id="lobbytable" class="list-group">
                        <li class="list-group-item list-group-item-empty">Open Slot</li>
                        <li class="list-group-item list-group-item-empty">Open Slot</li>
                        <li class="list-group-item list-group-item-empty">Open Slot</li>
                        <li class="list-group-item list-group-item-empty">Open Slot</li>
                    </ul>
                </div>
            <#else>
            <div class="text-center">
                <div class="text-danger">
                    Something went wrong, please try rejoining!
                </div>
                <button class="btn btn-primary" onclick="loadBoards()">Back</button>
            </div>
            </#if>
        </div>
        <div class="col-4">
            <div class="text-center">
                <small>Settings</small>
            </div>
            <div class="board-settings">
                <div>
                    <b>Size:</b> <span>${board.size}</span>
                </div>
                <div>
                    <b>Start:</b> <span>${board.start}</span>
                </div>
                <div>
                    <b>Goal:</b> <span>${board.goal}</span>
                </div>
                <div>
                    <b>Description:</b> <span>${board.description}</span>
                </div>
            </div>
        </div>
    </div>
</div>