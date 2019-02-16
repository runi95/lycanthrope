<div class="container">
    <#if lobby??>
        <div class="row">
            <div class="col-sm-12">
                <div class="text-center">
                    <h1>
                        ${lobby.name}
                    </h1>
                </div>
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-sm-12">
                <div class="text-center">
                    <small>Players in lobby</small>
                    <ul id="lobbytable" class="list-group">
                        <#list 1..lobby.lobbyMaxSize as i>
                            <#if lobby.currentPlayerSize < i>
                                <li class="list-group-item list-group-item-empty">Open Slot</li>
                            <#else>
                                <#if lobby.users[i - 1].id == user.id>
                                    <li id="${lobby.users[i - 1].id}" class="list-group-item list-group-item-success">${lobby.users[i - 1].nickname}</li>
                                <#else>
                                    <li id="${lobby.users[i - 1].id}" class="list-group-item list-group-item-default">${lobby.users[i - 1].nickname}</li>
                                </#if>
                            </#if>
                        </#list>
                    </ul>
                </div>
            </div>
        </div>
    <#else>
        <div class="row">
            <div class="col-sm-12">
                <div class="text-center">
                    <div class="text-danger">
                        Something went wrong, please try rejoining!
                    </div>
                    <button class="btn btn-primary" onclick="loadLobbies()">Back</button>
                </div>
            </div>
        </div>
    </#if>
</div>
<script>
    currentLobbyId = ${lobby.id};
</script>