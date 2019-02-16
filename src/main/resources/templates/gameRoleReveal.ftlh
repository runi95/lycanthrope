<div class="container">
    <div class="row">
        <div class="col-sm-12">
            <div class="text-center margin-top">
                <h3>Role Reveal</h3>
                <div class="progress" style="height: 1px;">
                    <div id="progressbar" class="progress-bar" role="progressbar" style="width: 100%;"></div>
                </div>
                <br>
                <div>
                    Your role is
                </div>
                <div style="background: black; min-height: 60px; width: 100%; max-width: 300px; margin: auto; word-break: break-all;"
                     onclick="revealRole()">
                    <h1 class="text-danger" id="rolediv"></h1>
                </div>
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
    var roleName = "${roleName}";
    var isRoleRevealed = false;

    function revealRole() {
        if (!isRoleRevealed) {
            isRoleRevealed = true;

            document.getElementById("rolediv").appendChild(document.createTextNode(roleName));
        }
    }
</script>