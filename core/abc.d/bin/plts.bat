@echo off

    setlocal
    call findabc plt_scheme .
    mzscheme %*
