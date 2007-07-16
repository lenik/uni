@echo off

    rem /d      update only
    rem /h      include hidden files
    rem /r      overwrite readonly files
    rem /y      don't prompt
    rem /e      empty directories

    setlocal

    set flags=/d /h /r /y
    set rest=
:prep
    if "%~1"=="" goto exec

    if "%1"=="-r" (
        set flags=%flags% /e
    ) else if "%1"=="-v" (
        set _verb=1
    ) else (
        set rest=%rest% %1
    )
    shift
    goto prep

:exec
    if "%_verb%"=="1" (
        echo xcopy %flags% %rest%
    )
    xcopy %flags% %rest%
