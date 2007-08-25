@echo off

    setlocal
    call findabc otp bin
    start %_home%\bin\werl %*
