<#macro websockmaster>
<!DOCTYPE html>
<html>
<head>
    <title>Lycanthrope</title>
    <meta name="description" content="The ultimate board game!">
    <meta charset="UTF-8">
    <meta name="keywords" content="gaming, board games, board, game, board game, space, worms, worm">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="HandheldFriendly" content="true">

    <#if _csrf??>
        <meta name="csrf_name" content="<#if _csrf.parameterName??>${_csrf.parameterName}</#if>">
        <meta name="csrf_value" content="<#if _csrf.token??>${_csrf.token}</#if>">
    </#if>
    <#if nickname??>
        <meta name="nickname" content="${nickname}">
    </#if>

    <script defer src="/webjars/jquery/2.1.4/jquery.min.js"></script>
    <script defer src="/webjars/bootstrap/4.1.0/js/bootstrap.min.js"></script>
    <script defer src="/js/webSocketMaster.js"></script>
    <script defer src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script defer src="/webjars/stomp-websocket/stomp.min.js"></script>
    <script defer src="/js/stomp.js"></script>
    <link rel="stylesheet"
          href="/webjars/bootstrap/4.1.0/css/bootstrap.min.css" async/>
    <link rel="stylesheet"
          href="/webjars/font-awesome/5.7.1/css/all.css" async>
    <link rel="stylesheet"
          href="/css/master.css" async/>
</head>
<body>
<div style="position: fixed; left: 10px; bottom: 6px; z-index: 1000;">
    Connection: <span id="connected" class="text-success text-hide">connected</span><span id="disconnected" class="text-danger">disconnected</span><span id="connecting" class="text-warning text-hide">connecting...</span>
</div>
<div id="nest">
    <#nested/>
</div>
</body>
</html>
</#macro>