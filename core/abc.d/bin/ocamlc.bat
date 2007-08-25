@echo off

    setlocal
    call findabc ocaml .
    %_home%\bin\ocamlc %*
