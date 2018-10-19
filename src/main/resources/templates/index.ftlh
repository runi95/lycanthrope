<#import "master.ftlh" as m>
<@m.master>
<div style="position: fixed; left: 10px; top: 10px;">
    Connection: <span id="connected" class="text-success text-hide">connected</span><span id="disconnected" class="text-danger text-hide">disconnected</span><span id="connecting" class="text-warning text-hide">connecting...</span>
</div>
<div class="container">
    <div class="row">
        <div class="col-8 offset-2">
            <div class="text-center">
                <h1>
                    Welcome to Space Worms!
                </h1>
            </div>
            <div class="col-8 offset-2">
                <div class="login-box">
                    <form id="loginForm" name="loginForm" action="/login" method="POST">
				        <#if _csrf??>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        </#if>
                        <div class="form-group">
                            <label for="nickname">Nickname</label>
                            <input name="nickname" type="text" class="form-control input-opaque" id="nickname"
                                   aria-describedby="nicknameHelp" placeholder="Enter Nickname"
                                   required>
                            <small id="nicknameHelp" class="form-text text-muted">This is the in-game nickname we'll
                                show other players.
                            </small>
                        </div>
                        <div class="d-flex">
                            <div class="margin-left-auto">
                                <button type="submit" class="btn btn-primary">Login</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<link rel="stylesheet" href="/css/index.css" async/>
</@m.master>