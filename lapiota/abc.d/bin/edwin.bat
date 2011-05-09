@echo off

    setlocal
    call findabc mit_scheme
    set PATH=%PATH%;%_home%\bin
    call wich scheme

    start scheme -library %_home%\lib -edwin -edit %*
