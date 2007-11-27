@echo off

    rem $Id$

    if "%1"=="" goto help

:start
    set _bwexec=%~dp0..\1\bw_console.exe

:loop
    for %%i in (%1) do (
        echo Wrapping %%i...

        set _origin=%%~ni0%%~xi
        set _ini=%%~ni.ini
        ren "%%i" "!_origin!"
        copy "%_bwexec%" "%%~ni.exe" >nul

        echo [%%~ni]                    >"!_ini!"
        echo SystemVersion=%_bwexec%    >>"!_ini!"
        echo RouteTo=!_origin!          >>"!_ini!"
        echo PrefixArgsCount=0          >>"!_ini!"
        echo ;PrefixArg0=               >>"!_ini!"
        echo SuffixArgsCount=0          >>"!_ini!"
        echo ;SuffixArg0=               >>"!_ini!"
        echo EnvironsCount=1            >>"!_ini!"
        echo Environ0=_BW_ORIGIN=%%i    >>"!_ini!"
        echo ShowRoutedName=0           >>"!_ini!"
        echo Disable=0                  >>"!_ini!"
        echo FindPath=0                 >>"!_ini!"
        echo Banner=0                   >>"!_ini!"
        echo Preout=The wrapped version of %%~ni >>"!_ini!"
        echo LogEnabled=1               >>"!_ini!"
    )
    shift
    if not "%1"=="" goto loop

    goto cleanup


:help
    echo [BINWRAP] Binary Executable Wrapper
    echo (Powered by BW_CONSOLE)
    echo Written by Snima Denik,  Oct 2004
    echo.
    echo Syntax:
    echo    binwrap executable-name


:cleanup
    set _bwexec=
    set _origin=
    set _ini=

:end
