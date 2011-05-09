@echo off

    setlocal

:GUI
    call findabc emacs
    "%_HOME%\bin\runemacs" %*
    exit /b

:CUI
    set _T=Emacs-%RANDOM%

    title %_T%
    rem sleep -m 500
    rem wm -v -hide %_T%

    wm -hide

    call findabc emacs bin
    emacs %*

    wm -show
