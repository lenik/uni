@echo off

    setlocal
    call findabc npp .

    set filelist=
:next
    if "%~1"=="" goto eol

    rem file may contains wildchars
    for %%i in ("%~1") do (
        set filelist=!filelist! "%%~dpnxi"
    )

    shift
    goto next
:eol

    start %_home%\notepad++ %filelist%
