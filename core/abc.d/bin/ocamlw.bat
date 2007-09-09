@echo off

    setlocal
    call findabc ocaml .
    set OCAMLLIB=%_home%\lib
    start %_home%\ocamlwin %*
