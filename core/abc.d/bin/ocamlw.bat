@echo off

    setlocal
    call findabc ocaml .
    start %_home%\ocamlwin %*
