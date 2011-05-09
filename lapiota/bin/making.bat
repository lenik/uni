@echo off

    set MAKING_TARGET=
    set MAKING_HOME=.
    set MAKING_INTERVAL=10
    set MAKING_COUNT=0

    if not "%1"=="" set MAKING_TARGET=%1
    if not "%2"=="" set MAKING_HOME=%2
    if not "%3"=="" set MAKING_INTERVAL=%3

    pushd "%MAKING_HOME%" >nul


:loop

    set /A MAKING_COUNT = MAKING_COUNT + 1

    echo Making #%MAKING_COUNT%

    make %MAKING_TARGET%

    sleep %MAKING_INTERVAL%

    goto loop
