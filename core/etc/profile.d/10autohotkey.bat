rem #Setup hotkeys

    setlocal

    call findabc autohotkey >nul
    if errorlevel 1 exit /b 1

    set _hotkey=%LAPIOTA%\etc\hotkey
    for %%f in (I:\Lapiota\etc\hotkey) do (
        if exist %%f set _hotkey=%%f
    )

    rem set path=%LAPIOTA%\abc.d\bin;%LAPIOTA%\usr\bin;%LAPIOTA%\local\bin;%path%
    rem set path=%PATH%;%LAPIOTA%\usr\lib;%LAPIOTA%\local\lib
    start %_home%\autohotkey %_hotkey%
