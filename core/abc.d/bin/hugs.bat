@echo off

    setlocal
    call findabc hugs .
    "%_home%\hugs" %*
