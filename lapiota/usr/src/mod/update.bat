@echo off

for /d %%d in (*) do (
    if exist "%%d\.svn" (
        echo Updating %%d...
        svn up "%%d"
    )
)
