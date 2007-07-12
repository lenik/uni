@echo off

    setlocal

    if "%~n1"=="cmd" goto chsh
    if "%~n1"=="CMD" goto chsh

    if "%~1"==":chsh" goto chsh
    goto start

        REM shell-replacer
      :chsh
        if "%~1"==":chsh" (
            waitpid -q %2
            shift
            shift
            REM set _final=start
            goto start
        )
        set _sh=%DIRT_HOME%\3\cmd.exe
        ppid
        start %_sh% /c %0 :chsh %errorlevel% %*
        exit

:start
    if not exist "%~1" (
        echo %1 isn't existed.
        goto end
    )
    set base=%~nx1

    echo disable sfc...

    rem do the copy
    call :copy "%~1" "%windir%\ServicePackFiles\i386\%base%"
    call :copy "%~1" "%windir%\System32\dllcache\%base%"
    call :copy "%~1" "%windir%\System32\%base%"
    call :copy "%~1" "%windir%\%base%"

    echo enable sfc...

    if not "%_final%"=="" (
        echo finalizing...
        %_final%
    )
    echo done.

    goto end

:copy
    if exist "%~2" (
        echo Overwriting %2...
        rem copy /y "%~1" "%~2" >nul
        wfpreplace  "%~2" "%~1" >nul
    )
    goto end

:end
