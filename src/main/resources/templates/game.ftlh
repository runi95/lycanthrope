<#import "master.ftlh" as m>
<@m.master>
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
    var progress = 100;
    var startTime = 10 * 60;
    var timeLeft = 65;
    var countdownTimerShown = false;
    var firstVibration = false;
    var intervalTimer = setInterval(updateProgressBar, 1000);

    function updateProgressBar() {
        timeLeft--;

        if (timeLeft <= 0) {
            clearInterval(intervalTimer);
        }

        if (timeLeft <= 10 && !firstVibration) {
            firstVibration = true;
            window.navigator.vibrate(200);
        }

        if (timeLeft <= 60) {
            if (!countdownTimerShown) {
                countdownTimerShown = true;
                document.getElementById("votecountContainer").setAttribute("style", "");
                document.getElementById("discusshint").setAttribute("style", "display: none");
                document.getElementById("votecount").innerText = timeLeft;
            } else {
                document.getElementById("votecount").innerText = timeLeft;
            }
        }

        var progressMade = 100 * (timeLeft / startTime);
        if (progress > progressMade) {
            progress = progressMade;

            var elem = document.getElementById("progressbar");
            elem.setAttribute("style", "width: " + progress + "%;");
            elem.setAttribute("aria-valuenow", progress);
        }
    }
</script>
</@m.master>