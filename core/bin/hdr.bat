@echo off
    rem $Id$

    if "%PAGER%"=="" (
        dumpbin /headers %*
    ) else (
        dumpbin /headers %* | %PAGER%
    )
