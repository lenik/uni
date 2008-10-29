@echo off

    call findabc msys

    if not "%~1"=="" goto exec

    REM set WD=%_home%
    REM set MSYSTEM=MINGW32
    set DISPLAY=

    set rxvtopts=-sl 2500 -fg #aaffaa -bg #003300 -sr
    set rxvtopts=%rxvtopts% -fn Courier-12 -tn msys -geometry 80x25
    %_home%\bin\rxvt %rxvtopts% -e /bin/bash --login -i

:exec
    cmd /c %_home%\bin\%*
    exit /b
