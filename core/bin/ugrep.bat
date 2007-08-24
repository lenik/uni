@echo off

    setlocal

    set FLAGS=-Pabo

    if "%~1"=="-?" (
        set _verb=1
        shift
    )

:prep
    if "%~1"=="" goto exec
    set A=%~1
    if "%A:~0,1%"=="-" (
        set FLAGS=%FLAGS% %~1
    ) else (
        set PATTERN=%~1
        set P=%~1
        shift
        goto prep2
    )
    shift
    goto prep

:prep2
    if "%P%"=="" goto prep3
    set UPAT=%UPAT%%P:~0,1%\x00
    set P=%P:~1%
    goto prep2

:prep3
    if not "%_verb%"=="1" goto prep4
    for /f %%i in ('call hex -d "%PATTERN%"') do set HEX=%%i
    echo HEX DUMP:
    echo    %HEX%
  :loop3
    if "%HEX%"=="" goto end3
    set UHEX=%UHEX%%HEX:~0,2%00
    set HEX=%HEX:~2%
    goto loop3
  :end3
    echo UNICODE-HEX DUMP:
    echo    %UHEX%

:prep4
    if "%~1"=="" goto exec
    set REST=%REST% %1
    shift
    goto prep4

:exec
    if "%_verb%"=="1" (
        echo grep %FLAGS% "%UPAT%" %REST%
    )
    grep %FLAGS% "%UPAT%" %REST%

:end
