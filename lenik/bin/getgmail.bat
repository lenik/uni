@echo off

    set trying=0

:loop
    set /a trying=trying+1
    echo Trying %trying%...

    so -r=www.bytetest.com -s="GET http://www.bytetest.com/invite.do\n\n" -g=print >ins

    tlines ins>ins.2

    grep -i "Invites Remaining" ins.2
    grep -i sorry ins.2

    if errorlevel 1 goto notfound

    goto end

:notfound

    start http://www.bytetest.com/invite.do
    lc user32::MessageBoxA(0, "Gotcha!", 0, 0)

:end

    goto loop
