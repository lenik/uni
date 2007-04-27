@echo off

    setlocal

    set vchome=C:\Program Files\Video Converter
    set path=%path%;%vchome%;%vchome%\codecs\

    set _debug=0
    set _prefix=m6-
    set _param=
    set _param=%_param% -ofps 18.000
    set _param=%_param% -vf-add crop=0:0:-1:-1
    set _param=%_param% -vf-add scale=320:240
    set _param=%_param% -vf-add expand=320:240:-1:-1:1
    set _param=%_param% -vf-add rotate=1
    set _param=%_param% -xvidencopts bitrate=384

    if not "%1"=="[" goto begin
:prep
    if "%1"=="]" (
        shift
        goto begin
    )

    set /a _num=%1
    if "%_num%"=="%1" if "%2"=="]" (
        echo set bitrate=%_num%
        set _prefix=r%_num%-
        set _param=-xvidencopts bitrate=%_num%
    )

    if "%1"=="k" (
        set _prefix=d-
        set _param=-xvidencopts bitrate=1000
    )

    rem more options...

    shift
    goto prep

:begin
    call libstr sn "%_prefix%"
    set _prefix_len=%_ret%

    set _fn=%~1
    if "%_fn:~0,1%"=="@" (
        set _fn=!_fn:~1!
        for /f "delims=?" %%i in (!_fn!) do (
            echo [@ batch] %%i
            call :main "%%i"
        )
        goto end
    )
    if "%_fn:~0,1%"=="*" (
        for %%i in (%_fn%) do (
            set _i_prefix=%%i
            set _i_prefix=!_i_prefix:~0,%_prefix_len%!
            if "!_i_prefix!"=="%_prefix%" (
                echo [* batch] skip %%i
            ) else (
                echo [* batch] %%i
                call :main "%%i"
            )
        )
        goto end
    )

:main
    set ext=.avi

    rem set font=?

    set menc_args=
    set menc_args=%menc_args% -mc 0
        set menc_args=%menc_args% -font "c:\windows\fonts\simsun.ttc"
    set menc_args=%menc_args% -srate 44100
    set menc_args=%menc_args% -ovc xvid
    set menc_args=%menc_args% -oac mp3lame
    set menc_args=%menc_args% -lameopts vbr=0
    set menc_args=%menc_args% -lameopts br=128
    set menc_args=%menc_args% -lameopts vol=0
    set menc_args=%menc_args% -lameopts mode=0
    set menc_args=%menc_args% -lameopts aq=7
    set menc_args=%menc_args% -lameopts padding=3
    set menc_args=%menc_args% -af volnorm
    set menc_args=%menc_args% -xvidencopts max_bframes=0:nogmc:noqpel
    set menc_args=%menc_args% %_param%

    set src=%~1
    set dst=%~dp1%_prefix%%~n1%ext%
    echo Convert %src% to %dst%...
    if "%_debug%"=="1" (
        echo mencoder -noodml "%src%" -o "%dst%" %menc_args%
        mencoder -noodml "%src%" -o "%dst%" %menc_args%
    ) else (
        echo mencoder -noodml "%src%" -o "%dst%" %menc_args% >_lastxvc.bat
        mencoder -noodml "%src%" -o "%dst%" %menc_args% 2>&1 |grep %% |pc -e
    )

:end
