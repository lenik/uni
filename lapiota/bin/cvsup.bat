@echo off

    pushd "%1" >nul
    cvs -q up
    popd >nul
