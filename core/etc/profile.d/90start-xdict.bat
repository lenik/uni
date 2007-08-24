rem #Start X-Dict

    set _VAROJ=C:\.radiko\varoj

    if not exist "%_VAROJ%\Help\Kingsoft PowerWord 2005\xdict.exe" exit /b 1

    cd /d "%_VAROJ%\Help\Kingsoft PowerWord 2005"
    start xdict.exe
