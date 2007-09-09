@echo off

    setlocal
    call findabc ocaml .
    set OCAMLLIB=%_home%\lib
    %_home%\bin\ocamlc %*
