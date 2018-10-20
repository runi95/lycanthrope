<div class="container">
    <div class="row">
        <div class="col-8 offset-2">
            <div class="text-center">
                <#if board??>
                    <h1>
                        You are now in ${board.name}
                    </h1>
                    <ul id="lobbytable" class="list-group"></ul>
                <#else>
                    <div class="text-danger">
                        Something went wrong, please try rejoining!
                    </div>
                    <button class="btn btn-primary" onclick="loadBoards()">Back</button>
                </#if>
            </div>
        </div>
    </div>
</div>