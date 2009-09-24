@echo off

    setlocal
    call findabc npp
    if errorlevel 1 (
        echo Can't find notepad++.
        exit /b 1
    )
    set PATH=%PATH%;%_home%

    if "%~1"=="--help" goto help
    if "%~1"=="-h" goto help

    set filelist=
    if "%~1"=="-" (
        set _enter=1
        shift
    )

  :next
    set _f=
    if "%_enter%"=="1" (
        set /p _f=File:
    ) else (
        set _f=%~1
        shift
    )
    if "%_f%"=="" goto eol
    rem file may contains wildchars
    for %%i in ("%_f%") do (
        set filelist=!filelist! "%%~dpnxi"
    )
    goto next
  :eol

    start %_home%\notepad++ %filelist%
    exit /b

:help
    echo no [- ^| FILELIST]
    exit /b
