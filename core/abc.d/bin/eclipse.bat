@echo off

    setlocal

:find_java
    if not "%JAVA_HOME%"=="" goto start
    call findabc jdk
    set JAVA_HOME=%_home%

:start
    set PATH=%JAVA_HOME%\bin;%JAVA_HOME%\jre\bin;%PATH%
    call findabc eclipse .
    start %_home%\eclipse %*
