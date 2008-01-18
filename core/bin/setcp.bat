@echo off

    for /f "delims== tokens=1,2" %%i in (%JAVA_LIB%\versions.property) do (
        set _k=%%i
        set _v=%%j
        set _k=!_k: =!
        set _v=!_v: =!
        set !_k!=!_v!
    )

    if "%~1"=="-c"      set CLASSPATH=.
    if "%~1"=="--clear" set CLASSPATH=.

    set CLASSPATH=%CLASSPATH%;%JAVA_LIB%\%libfreejava%
    set CLASSPATH=%CLASSPATH%;%JAVA_LIB%\%liblapiota%
    set CLASSPATH=%CLASSPATH%;%JAVA_LIB%\%libapc_collections%
