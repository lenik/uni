@echo off

    setlocal

    set url=%~1
    set url=%url:\=/%
    set base=%~nx1

    md "%base%"
    svn co "file:///%url%/trunk" "%base%"
