@echo off

    setlocal
    call findabc hugs .
    start %_home%\winhugs %*
