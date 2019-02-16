<div class="container">
    <div class="row">
        <div class="col-sm-12">
            <div class="margin-top text-center">
                <h3>Vote</h3>
                <div class="progress" style="height: 1px;">
                    <div id="progressbar" class="progress-bar" role="progressbar" style="width: 100%;"
                         aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
            </div>
            <br>
            <div class="margin-top">
                <#list lobby.users as user>
                    <#if user.id == userid>
                        <button id="u${user.id}" class="btn btn-block btn-outline-secondary"
                                disabled="true">${user.nickname} <span
                                    id="bu${user.id}"
                                    class="badge badge-pill badge-light">${user.player.votesAgainstPlayer}</span>
                        </button>
                    <#else>
                        <#if vote?? && "u${user.id}" == vote>
                            <button id="u${user.id}" class="btn btn-block btn-secondary"
                                    onclick="performVoteAction(this)">${user.nickname} <span
                                        id="bu${user.id}"
                                        class="badge badge-pill badge-light">${user.player.votesAgainstPlayer}</span>
                            </button>
                        <#else>
                            <button id="u${user.id}" class="btn btn-block btn-primary"
                                    onclick="performVoteAction(this)">${user.nickname} <span
                                        id="bu${user.id}"
                                        class="badge badge-pill badge-light">${user.player.votesAgainstPlayer}</span>
                            </button>
                        </#if>
                    </#if>
                </#list>
            </div>
        </div>
    </div>
</div>
<style>
    .margin-top {
        margin-top: 10px;
    }
</style>
<script>
    function performVoteAction(elem) {
        voteAction(elem.id);
    }
</script>