@echo off

    setlocal
    call findabc mit_scheme bin
    start scheme -library %_home%\lib -edwin -edit %*
