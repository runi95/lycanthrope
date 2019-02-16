<div class="container">
    <div class="row">
        <div class="col-sm-12">
            <div class="margin-top text-center">
                <h3>Discussion Time</h3>
                <div class="progress" style="height: 1px;">
                    <div id="progressbar" class="progress-bar" role="progressbar" style="width: 100%;"
                         aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
            </div>
            <#if secretMessage ??>
                <div id="secretmsg">
                    <br>
                    <div class="text-center"
                         style="background: black; padding: 20px; width: 100%; max-width: 300px; margin: auto; word-break: break-word;"
                         onclick="hideSecretMessage()">
                        <h4 class="text-info">
                            ${secretMessage}
                        </h4>
                        <small class="text-secondary">Click to hide message</small>
                    </div>
                </div>
            </#if>
            <br>
            <div class="margin-top text-center">
                <div id="votecountContainer" style="display: none;">
                    <small>Vote in...</small>
                    <h1 id="votecount" class="text-danger"></h1>
                </div>
                <small id="discusshint">
                    Discuss until the timer runs out...
                </small>
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
    function hideSecretMessage() {
        var elem = document.getElementById("secretmsg");
        if (elem) {
            elem.remove();
        }
    }
</script>