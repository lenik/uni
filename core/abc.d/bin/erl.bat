@echo off

    setlocal
    call findabc otp bin

    set /a _t=360000*(1%time:~0,2%-100) + 6000*(1%time:~3,2%-100) + 100*(1%time:~6,2%-100) + (1%time:~9,2%-100)

        %_home%\bin\%~n0 %*
        if not errorlevel 1 exit /b

    set /a _t=360000*(1%time:~0,2%-100) + 6000*(1%time:~3,2%-100) + 100*(1%time:~6,2%-100) + (1%time:~9,2%-100) - _t

    rem try again if last command failed immediately
    call .erl_init %_home%\bin\erl.ini
    %_home%\bin\%~n0 %*
