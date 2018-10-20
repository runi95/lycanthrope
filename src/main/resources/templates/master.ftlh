<#macro master>
<!DOCTYPE html>
<html>
<head>
    <title>Space Worms</title>
    <meta name="description" content="The ultimate board game!">
    <meta charset="UTF-8">
    <meta name="keywords" content="gaming, board games, board, game, board game, space, worms, worm">

    <#if _csrf??>
        <meta name="csrf_name" content="<#if _csrf.parameterName??>${_csrf.parameterName}</#if>">
        <meta name="csrf_value" content="<#if _csrf.token??>${_csrf.token}</#if>">
    </#if>

    <script defer src="/webjars/jquery/2.1.4/jquery.min.js"></script>
    <script defer src="/webjars/bootstrap/4.1.0/js/bootstrap.min.js"></script>
    <script defer src="/js/master.js"></script>
    <script defer src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script defer src="/webjars/stomp-websocket/stomp.min.js"></script>
    <script defer src="/js/stomp.js"></script>
    <link rel="stylesheet"
          href="/webjars/bootstrap/4.1.0/css/bootstrap.min.css" async/>
    <link rel="stylesheet"
          href="/css/master.css" async/>
</head>
<body>
<div id="nest">
    <#nested/>
</div>
</body>
</html>
</#macro>