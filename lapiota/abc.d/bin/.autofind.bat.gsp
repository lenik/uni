@echo off
    <%
        assert name != null;
        if (sub == null) sub = name;
        if (target == null) target = name;
        if (ext == null) ext = ".exe";
        if (exec == null) exec = "";
    %>
    setlocal

    set name=<%= name %>
    set target=<%= target %>
    set defext=<%= ext %>

    call findabc <%= sub %>
    <% if (!exec.isEmpty()) { %>if "%exec%"=="" set exec=<%= exec %><% } %>
    set defexec=start "%_home%" <%= exec %>
    if "%exec%"=="" if "%cd%"=="%USERPROFILE%" set exec=%defexec%

    if exist "%_home%\%target%%defext%" goto _implicit
    for /d %%i in (%_home%\*) do (
        set bindir=%%~nxi
        if exist "%_home%\%%~nxi\%target%%defext%" goto _bin
    )
    echo Can't find the target of %target% under %_home%.
    exit /b 1

:_implicit
    %exec% "%_home%\%target%" %*
    exit /b

:_bin
    %exec% "%_home%\%bindir%\%target%" %*
    exit /b
