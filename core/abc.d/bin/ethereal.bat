@echo off

    setlocal
    call findabc ethereal .
    start %_home%\ethereal %*
