@echo off

    setlocal

    if not "%CODEPAGE%"=="" goto initmsg

    for /f "tokens=1,2" %%i in ('chcp') do (
        set CODEPAGE=%%j
        goto initmsg
    )

    set CODEPAGE=1021

:initmsg
    goto init_%CODEPAGE%

:init_936
    set l_sm=「开始」菜单
    set l_programs=程序
    set l_startup=启动
    goto start

:init_1021
    set l_sm=Start Menu
    set l_programs=Programs
    set l_startup=Startup
    goto start

:start
    set _startupd=%USERPROFILE%\%l_sm%\%l_programs%\%l_startup%
    echo copy lapiota-boot.bat "%_startupd%"
    copy lapiota-boot.bat "%_startupd%" >nul
