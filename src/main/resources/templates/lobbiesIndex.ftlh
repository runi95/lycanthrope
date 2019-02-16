<#import "webSocketMaster.ftlh" as m>
<@m.websockmaster>
    <div class="container">
        <div class="row">
            <div class="col-sm-12 col-md-4 offset-md-4">
                <div class="text-center animate-top-slowly">
                    <div id="loader" class="loader">
                        <div id="connection">
                                <span class="fas fa-sync-alt fa-spin"></span> Connecting...
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <style>
        .loader {
            border-radius: 10px;
            line-height: 60px;
            height: 60px;
            width: 100%;
            margin: auto;
        }
    </style>
</@m.websockmaster>