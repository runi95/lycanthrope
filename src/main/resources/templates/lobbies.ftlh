<div class="container">
    <div class="row">
        <div class="col-sm-12">
            <div class="text-center">
                <h1>
                    Lycanthrope
                </h1>
                <hr>
                <button class="btn btn-block btn-primary" onclick="getCreateLobbyView()">Create Lobby</button>
                <br>
                <div id="lobbytable" class="d-flex flex-column">
                    <#list lobbyList as lobby>
                        <div>
                            <h4>
                                ${lobby.name}
                            </h4>
                            <hr>
                            <div id="${lobby.id}s">
                                ${lobby.currentPlayerSize}/${lobby.lobbyMaxSize}
                            </div>
                            <button class="btn btn-info" onclick="joinLobbyRequest(${lobby.id})">
                                Join
                            </button>
                        </div>
                    </#list>
                </div>
            </div>
        </div>
    </div>
</div>