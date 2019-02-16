<div class="container">
    <div class="row">
        <div class="col-sm-12">
            <div class="margin-top text-center">
                <h3>Shoot</h3>
                <div class="progress" style="height: 1px;">
                    <div id="progressbar" class="progress-bar" role="progressbar" style="width: 100%;"
                         aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
            </div>
            <br>
            <div class="margin-top">
                <#list users as user>
                    <button id="u${user.id}" class="btn btn-block btn-primary"
                            onclick="performHunterKill(this)">${user.nickname}</button>
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
    function performHunterKill(elem) {
        hunterKill(elem.id);
    }
</script>