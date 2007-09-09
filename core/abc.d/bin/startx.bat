@echo off

    setlocal

    call wm -hide

    %SHELL% -c "/usr/X11R6/bin/startxwin.sh &"
