@echo off

    for %%f in (%*) do (
        set CLASSPATH=!CLASSPATH!;%%~ff
    )
