@echo off
    setlocal

    set _jar=%~1
    set _size=%~z1
    set _x=%temp%\%random%.zi
    set _xc=%_x%c

    jar -tf "%1" >%_x%

    wc "%_x%" >%_xc%
    for /f %%i in (%_xc%) do set _files=%%i

    grep .java %_x% | wc >%_xc%
    for /f %%i in (%_xc%) do set _javas=%%i

    grep .class %_x% | grep -v \$ | wc >%_xc%
    for /f %%i in (%_xc%) do set _classes=%%i

    del %_x% >nul 2>nul
    del %_xc% >nul 2>nul

    echo %_size%:%_files%:%_javas%:%_classes%:%_jar%
