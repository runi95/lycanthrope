<div class="container">
    <div class="row">
        <div class="col-sm-12">
            <div class="margin-top text-center">
                <h3>Night Action</h3>
                <div class="progress" style="height: 1px;">
                    <div id="progressbar" class="progress-bar" role="progressbar" style="width: 100%;"
                         aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
            </div>
            <br>
            <#if viewRole ??>
                <div class="text-center">
                    <small>Your target's role was...</small>
                    <h1>
                        ${viewRole}
                    </h1>
                </div>
            </#if>
            <#if smallMessage??>
                <div class="text-center">
                    <small>
                        ${smallMessage}
                    </small>
                </div>
            </#if>
            <#if message??>
                <div class="text-center">
                    ${message}
                </div>
            </#if>
            <#if nightAction ??>
                <#if nightAction.isAbleToTargetOtherPlayers()>
                    <div class="margin-top">
                        <#list lobby.users as user>
                            <#if user.id != userid || nightAction.isAbleToTargetSelf()>
                                <#if !(firstTarget??) || firstTarget != "u${user.id}">
                                    <button id="u${user.id}" class="btn btn-block btn-primary"
                                            onclick="performNightAction(this)">${user.nickname}</button>
                                </#if>
                            </#if>
                        </#list>
                    </div>
                </#if>
                <#if nightAction.isAbleToViewCertainRoles()>
                    <#list lobby.users as user>
                        <#list nightAction.getViewableRoles() as viewableRole>
                            <#if user.player.roleId == viewableRole.ordinal()>
                                <div class="btn btn-block btn-secondary">${user.nickname}</div>
                            </#if>
                        </#list>
                    </#list>
                </#if>
                <#if nightAction.isAbleToTargetNeutrals()>
                    <hr>
                    <div>
                        <#if !(firstTarget??) || firstTarget != "n1">
                            <button id="n1" class="btn btn-block btn-info" onclick="performNightAction(this)">First
                                Neutral
                            </button>
                        </#if>
                        <#if !(firstTarget??) || firstTarget != "n2">
                            <button id="n2" class="btn btn-block btn-info" onclick="performNightAction(this)">Second
                                Neutral
                            </button>
                        </#if>
                        <#if !(firstTarget??) || firstTarget != "n3">
                            <button id="n3" class="btn btn-block btn-info" onclick="performNightAction(this)">Third
                                Neutral
                            </button>
                        </#if>
                    </div>
                </#if>
            </#if>
        </div>
    </div>
</div>
<style>
    .margin-top {
        margin-top: 10px;
    }
</style>
<script>
    /*
    var progress = 100;
    var intervalInMilliseconds = 1000 / 100 * 60;
    var intervalTimer = setInterval(updateProgressBar, intervalInMilliseconds);

    function updateProgressBar() {
        if (progress > 0) {
            var elem = document.getElementById("progressbar");
            progress--;
            elem.setAttribute("style", "width: " + progress + "%;");
            elem.setAttribute("aria-valuenow", progress);
        } else {
            clearInterval(intervalTimer);
        }
    }
    */

    function performNightAction(elem) {
        nightAction(elem.id);
    }
</script>