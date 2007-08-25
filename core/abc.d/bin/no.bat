@echo off

    setlocal
    call findabc npp .
    start %_home%\notepad++ %*
