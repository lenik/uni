@echo off

echo WARNING:
echo This will delete all files except .svn/ directories,
echo Are you still want to continue?
echo Press Ctrl-C to exit.
pause

for /d /r %i in (*) do set j=%i& if "%i"=="!j:.svn=!" (echo %i& del /q %i)
