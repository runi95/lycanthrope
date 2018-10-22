<#import "master.ftlh" as m>
<@m.master>
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
                    <form id="loginForm" name="loginForm" action="/setNick" method="POST">
				        <#if _csrf??>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        </#if>
                        <div class="form-group<#if error??> has-error has-feedback</#if>">
                            <label for="nickname">Nickname</label>
                            <input name="nickname" type="text" class="form-control input-opaque" id="nickname"
                                   aria-describedby="nicknameHelp" placeholder="Enter Nickname*"
                                   required autofocus maxlength="16" pattern="^[\w\-']+$">
                            <small id="nicknameHelp" class="form-text text-muted">*A nickname can only contain english alphanumeric characters</small>
                            <#if error??><small class="text-danger">${error}</small></#if>
                        </div>
                        <div class="d-flex">
                            <div class="margin-left-auto">
                                <button type="submit" class="btn btn-primary">Join</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<link rel="stylesheet" href="/css/index.css" async/>
<script defer src="/js/index.js"></script>
</@m.master>