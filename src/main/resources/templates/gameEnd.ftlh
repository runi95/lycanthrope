<#import "master.ftlh" as m>
<@m.master>
    <div class="container">
        <div class="row">
            <div style="position: relative; width: 100%;">
                <small class="text-secondary text-center" style="width: 100%; position: fixed; bottom: 5px;">${gameEndTime}</small>
            </div>
            <div class="col-sm-12">
                <div class="text-center margin-top">
                    <h1>
                        Result
                    </h1>
                    <hr>
                    <#list gameresult.gameResultPlayers as playerGameResult>
                        <div>
                            <#if playerGameResult.dead><i class="fas fa-skull"></i> </#if>${playerGameResult.nickname} <span class="text-secondary">(${playerGameResult.role})</span> <#if playerGameResult.winner><span class="text-success">WON</span><#else><span class="text-danger">LOST</span></#if>
                        </div>
                    </#list>
                </div>
            </div>
        </div>
    </div>
    <style>
        .margin-top {
            margin-top: 16px;
        }

        .text-middle {
            margin: auto;
            text-align: center;
        }

        .fade-in {
            animation: opac 10s;
        }

        @keyframes opac {
            0% { opacity: 0; }
            25% { opacity: 0.14; }
            50% { opacity: 0.29; }
            75% { opacity: 0.5; }
            100% { opacity: 1; }
        }
    </style>
</@m.master>