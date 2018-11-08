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
            <div id="stage-1" style="display: none;">
                <div>
                    How many players are you?
                </div>
                <button class="btn btn-block btn-primary">3</button>
                <button class="btn btn-block btn-primary">4</button>
                <button class="btn btn-block btn-primary">5</button>
                <button class="btn btn-block btn-primary">6</button>
                <button class="btn btn-block btn-primary">7</button>
                <button class="btn btn-block btn-primary">8</button>
                <button class="btn btn-block btn-primary">9</button>
                <button class="btn btn-block btn-primary">10</button>
            </div>
            <div class="margin-bottom" id="stage-2">
                <div>
                    Which roles should be in play?
                </div>
                <button class="btn btn-block btn-primary">Hunter</button>
                <button class="btn btn-block btn-primary">Beholder</button>
                <button class="btn btn-block btn-primary">Drunk</button>
                <button class="btn btn-block btn-primary">Insomniac</button>
                <button class="btn btn-block btn-primary">Squire</button>
                <button class="btn btn-block btn-primary">Aura Seer</button>
                <button class="btn btn-block btn-primary">Troublemaker</button>
                <button class="btn btn-block btn-primary">Robber</button>
                <button class="btn btn-block btn-primary">Seer</button>
                <button class="btn btn-block btn-primary">Thing</button>
                <button class="btn btn-block btn-primary">Mason 1</button>
                <button class="btn btn-block btn-primary">Mason 2</button>
                <button class="btn btn-block btn-primary">Apprentice Tanner</button>
                <button class="btn btn-block btn-primary">Minion</button>
                <button class="btn btn-block btn-primary">Dopplegänger</button>
                <button class="btn btn-block btn-primary">Villager 1</button>
                <button class="btn btn-block btn-primary">Villager 2</button>
                <button class="btn btn-block btn-primary">Villager 3</button>
                <button class="btn btn-block btn-primary">Werewolf 1</button>
                <button class="btn btn-block btn-primary">Werewolf 2</button>
                <button class="btn btn-success">Done</button>
            </div>
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
    var recommendedRoleSetups = {
        3: ["werewolf1", "werewolf2", "robber", "tanner", "drunk", "doppelganger"],
        4: ["werewolf1", "werewolf2", "robber", "tanner", "doppelganger", "villager1", "seer"],
        5: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "doppelganger"],
        6: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "drunk", "villager1"],
        7: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "drunk", "villager1", "villager2"],
        8: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "drunk", "villager1", "villager2", "hunter"],
        9: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "drunk", "villager1", "villager2", "villager3", "hunter"],
        10: ["werewolf1", "werewolf2", "robber", "tanner", "seer", "troublemaker", "minion", "drunk", "villager1", "villager2", "hunter", "mason1", "mason2"],
    }
</script>
</@m.master>