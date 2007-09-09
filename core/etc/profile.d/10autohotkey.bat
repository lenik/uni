rem #Setup hotkeys

    call findabc autohotkey >nul
    if errorlevel 1 exit /b 1

    rem set path=%LAPIOTA%\abc.d\bin;%LAPIOTA%\usr\bin;%LAPIOTA%\local\bin;%path%
    rem set path=%PATH%;%LAPIOTA%\usr\lib;%LAPIOTA%\local\lib
    start %_home%\autohotkey %LAPIOTA%\etc\hotkey
