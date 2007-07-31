@echo off

    spawn chmdecoder -menu %1 .
    waitpid %errorlevel%
