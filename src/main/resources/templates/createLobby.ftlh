<#import "master.ftlh" as m>
<@m.master>
<div class="container">
    <div class="row">
        <div class="col-sm-12">
            <div class="margin-top text-center">
                <h3>Create Lobby</h3>
                <div class="progress" style="height: 1px;">
                    <div class="progress-bar" role="progressbar" style="width: 0%;" aria-valuenow="0" aria-valuemin="0"
                         aria-valuemax="100"></div>
                </div>
            </div>
            <br>
            <div class="margin-bottom" id="stage-1">
                <div>
                    How many players are you?
                </div>
                <button class="btn btn-block btn-primary" onclick="setPlayers(3)">3</button>
                <button class="btn btn-block btn-primary" onclick="setPlayers(4)">4</button>
                <button class="btn btn-block btn-primary" onclick="setPlayers(5)">5</button>
                <button class="btn btn-block btn-primary" onclick="setPlayers(6)">6</button>
                <button class="btn btn-block btn-primary" onclick="setPlayers(7)">7</button>
                <button class="btn btn-block btn-primary" onclick="setPlayers(8)">8</button>
                <button class="btn btn-block btn-primary" onclick="setPlayers(9)">9</button>
                <button class="btn btn-block btn-primary" onclick="setPlayers(10)">10</button>
            </div>
            <div class="margin-bottom" id="stage-2" style="display: none;">
                <div>
                    Which roles should be in play?
                </div>
                <button class="btn btn-block btn-primary" id="hunter" onclick="setRole(this)">Hunter</button>
                <button class="btn btn-block btn-primary" id="drunk" onclick="setRole(this)">Drunk</button>
                <button class="btn btn-block btn-primary" id="insomniac" onclick="setRole(this)">Insomniac</button>
                <!-- <button class="btn btn-block btn-primary" id="troublemaker" onclick="setRole(this)">Troublemaker</button> -->
                <button class="btn btn-block btn-primary" id="robber" onclick="setRole(this)">Robber</button>
                <button class="btn btn-block btn-primary" id="seer" onclick="setRole(this)">Seer</button>
                <button class="btn btn-block btn-primary" id="mason1" onclick="setRole(this)">Mason 1</button>
                <button class="btn btn-block btn-primary" id="mason2" onclick="setRole(this)">Mason 2</button>
                <button class="btn btn-block btn-primary" id="tanner" onclick="setRole(this)">Tanner</button>
                <button class="btn btn-block btn-primary" id="minion" onclick="setRole(this)">Minion</button>
                <button class="btn btn-block btn-primary" id="doppelganger" onclick="setRole(this)">Doppelgänger</button>
                <button class="btn btn-block btn-primary" id="villager1" onclick="setRole(this)">Villager 1</button>
                <button class="btn btn-block btn-primary" id="villager2" onclick="setRole(this)">Villager 2</button>
                <button class="btn btn-block btn-primary" id="villager3" onclick="setRole(this)">Villager 3</button>
                <button class="btn btn-block btn-primary" id="werewolf1" onclick="setRole(this)">Werewolf 1</button>
                <button class="btn btn-block btn-primary" id="werewolf2" onclick="setRole(this)">Werewolf 2</button>
                <hr>
                <div class="text-danger" id="errorlabel"></div>
                <button class="btn btn-block btn-success" id="btndone" onclick="createNewLobby(activeRoles, players)">Create Lobby</button>
            </div>
            <br>
        </div>
    </div>
</div>
<style>
    .margin-top {
        margin-top: 10px;
    }

    .margin-bottom {
        margin-bottom: 10px;
    }
</style>
<script>
    var players;
    var roles = 0;
    var stage = 1;

    var recommendedRoleSetups = {
        3: ["werewolf1", "werewolf2", "robber", "tanner", "drunk", "troublemaker"],
        4: ["werewolf1", "werewolf2", "robber", "tanner", "troublemaker", "villager1", "seer"],
        5: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "hunter"],
        6: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "drunk", "villager1"],
        7: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "drunk", "villager1", "villager2"],
        8: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "drunk", "villager1", "villager2", "hunter"],
        9: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "drunk", "villager1", "villager2", "villager3", "hunter"],
        10: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "drunk", "villager1", "villager2", "hunter", "mason1", "mason2"]
    };

    var activeRoles = {
        "hunter": false,
        "drunk": false,
        "insomniac": false,
        "troublemaker": false,
        "robber": false,
        "seer": false,
        "mason1": false,
        "mason2": false,
        "tanner": false,
        "minion": false,
        "doppelganger": false,
        "villager1": false,
        "villager2": false,
        "villager3": false,
        "werewolf1": false,
        "werewolf2": false
    };

    function setPlayers(playerCount) {
        players = playerCount;

        var arr = recommendedRoleSetups[playerCount];
        for (var i = 0; i < arr.length; i++) {
            setRole(document.getElementById(arr[i]));
        }

        setStage(2);
    }

    function setStage(stageNumber) {
        stage = stageNumber;

        for (var i = 1; i <= 2; i++) {
            if (i === stage) {
                document.getElementById("stage-" + i).setAttribute("style", "");
            } else {
                document.getElementById("stage-" + i).setAttribute("style", "display: none;");
            }
        }
    }

    function setRole(btn) {
        var isActive = activeRoles[btn.id];
        activeRoles[btn.id] = !isActive;

        if (isActive) {
            btn.setAttribute("class", "btn btn-block btn-primary");
            roles--;
        } else {
            btn.setAttribute("class", "btn btn-block btn-secondary");
            roles++;
        }

        var btnDone = document.getElementById("btndone");
        if (roles === (players + 3)) {
            document.getElementById("errorlabel").innerText = "";

            btnDone.setAttribute("class", "btn btn-block btn-success");
            btnDone.disabled = false;
        } else {
            document.getElementById("errorlabel").innerText = "You have selected " + roles + "/" + (players + 3) + " roles";

            btnDone.setAttribute("class", "btn btn-block btn-secondary");
            btnDone.disabled = true;
        }
    }
</script>
</@m.master>