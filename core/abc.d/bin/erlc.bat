@echo off

    setlocal
    call findabc otp bin
    %_home%\bin\%~n0 %*
