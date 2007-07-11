@echo off

    setlocal

    if not exist "%~1" (
        echo %1 isn't existed.
        goto end
    )
    set base=%~nx1

    echo disable sfc...

    rem do the copy
    if exist "%windir%\ServicePackFiles\i386\%base%" (
        echo copy to %windir%\ServicePackFiles\i386
        copy /y "%~1" "%windir%\ServicePackFiles\i386" >nul
    )
    if exist "%windir%\System32\dllcache\%base%" (
        echo copy to %windir%\System32\dllcache
        copy /y "%~1" "%windir%\System32\dllcache" >nul
    )
    if exist "%windir%\System32\%base%" (
        echo copy to %windir%\System32
        copy /y "%~1" "%windir%\System32" >nul
    )
    if exist "%windir%\%base%" (
        echo copy to %windir%
        copy /y "%~1" "%windir%" >nul
    )

    echo enable sfc...

    echo done.

:end
