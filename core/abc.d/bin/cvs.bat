@echo off

    setlocal
    call findabc cvs-2 .
    %_home%\cvs %*
