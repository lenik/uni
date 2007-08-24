@echo off

    setlocal
    call findabc foobar .
    start foobar2000 %*
