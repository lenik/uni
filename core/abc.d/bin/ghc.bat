@echo off

    setlocal
    call findabc ghc . bin
    %_home%\bin\ghc %*
