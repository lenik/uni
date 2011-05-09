@echo off

if "%1"=="" (
    echo set id / serial images
    echo syntax: sid FILENAMES
    exit /b 1
)

renum -D -w 2 %*
