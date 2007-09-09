@echo off

    setlocal
    call findabc mencoder
    %_HOME%\mencoder.exe %*
