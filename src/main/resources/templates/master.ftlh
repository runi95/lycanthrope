<#macro master>
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

    <script defer src="/webjars/jquery/2.1.4/jquery.min.js"></script>
    <script defer src="/webjars/bootstrap/4.1.0/js/bootstrap.min.js"></script>
    <script defer src="/js/master.js"></script>
    <link rel="stylesheet"
          href="/webjars/bootstrap/4.1.0/css/bootstrap.min.css" async/>
    <link rel="stylesheet"
          href="/webjars/font-awesome/5.7.1/css/all.css" async>
    <link rel="stylesheet"
          href="/css/master.css" async/>
</head>
<body>
    <#nested/>
</div>
</body>
</html>
</#macro>