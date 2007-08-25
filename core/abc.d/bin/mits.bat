@echo off

    setlocal
    call findabc mit_scheme bin
    scheme -library %_home%\lib %*
