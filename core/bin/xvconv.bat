@echo off

    setlocal

    set vchome=C:\Program Files\Video Converter
    set path=%path%;%vchome%;%vchome%\codecs\

    set _debug=0
    set _prefix=m6-
    set _rotate=1

    if not "%1"=="[" goto begin
:prep
    if "%1"=="]" (
        shift
        goto begin
    )

    if "%1"=="d" (
        set _prefix=d-
        set _rotate=0
    )

    rem more options...

    shift
    goto prep

:begin
    set _fn=%~1
    if "%_fn:~0,1%"=="@" (
        set _fn=!_fn:~1!
        for /f "delims=?" %%i in (!_fn!) do (
            echo [batch] %%i
            call :main "%%i"
        )
        goto end
    )

:main
    set ext=.avi

    set menc_args=
    set menc_args=%menc_args% -mc 0
    set menc_args=%menc_args% -ofps 18.000
    set menc_args=%menc_args% -vf-add crop=0:0:-1:-1
    set menc_args=%menc_args% -vf-add scale=320:240
    set menc_args=%menc_args% -vf-add expand=320:240:-1:-1:1
    set menc_args=%menc_args% -vf-add rotate=%_rotate%
    set menc_args=%menc_args% -srate 44100
    set menc_args=%menc_args% -ovc xvid
    set menc_args=%menc_args% -xvidencopts bitrate=384
    set menc_args=%menc_args% -oac mp3lame
    set menc_args=%menc_args% -lameopts vbr=0
    set menc_args=%menc_args% -lameopts br=128
    set menc_args=%menc_args% -lameopts vol=0
    set menc_args=%menc_args% -lameopts mode=0
    set menc_args=%menc_args% -lameopts aq=7
    set menc_args=%menc_args% -lameopts padding=3
    set menc_args=%menc_args% -af volnorm
    set menc_args=%menc_args% -xvidencopts max_bframes=0:nogmc:noqpel

    set src=%~1
    set dst=%~dp1%_prefix%%~n1%ext%
    echo Convert %src% to %dst%...
    if "%_debug%"=="1" (
        mencoder -noodml "%src%" -o "%dst%" %menc_args%
    ) else (
        mencoder -noodml "%src%" -o "%dst%" %menc_args% 2>&1 |grep %% |pc -e
    )

:end
