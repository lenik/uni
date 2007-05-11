@echo off

    setlocal

    spawn %*
    set pid=%errorlevel%

    waitpid %pid%
