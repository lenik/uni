@echo off

    setlocal
    goto fix_method_2

:fix_method_1
    call findabc mplayer/
    cd codecs
    ..\mplayer.exe %*
    exit /b

:fix_method_2
    call findabc mplayer codecs
    for %%i in (%_home%\codecs\*) do (
        if not exist %_home%\%%~nxi (
            echo BUGFIX: mplayer can't find the codec %%~nxi, so copy it.
            copy %%i %_home%\%%~nxi >nul
        )
    )
    %_home%\mplayer.exe %*
