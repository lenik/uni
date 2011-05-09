@echo off

    if "%~1"=="" (
        if exist .env.as call :main .env.as
        goto end
    )
    if exist "%~1\*" (
        call :main "%~1\.env.as"
        goto end
    )

:main
    if not exist "%~dpnx1" (
        echo file %~1 isn't existed.
        exit /b 1
    )

    for /f "tokens=1-2*" %%i in (%~fs1) do (
        if not "%%i"=="#" (
            rem echo var=%%i op=%%j param=%%k
            if "%%j"=="=" (
                set %%i=%%k
            ) else if "%%j"==":" (
                if "!%%i!"=="" set %%i=%%k
            ) else if "%%j"=="+" (
                set %%i=!%%i!%%k
            ) else if "%%j"=="-" (
                set %%i=%%k!%%i!
            ) else if "%%j"=="p" (
                set %%i=!%%i!;%%k
            ) else if "%%j"=="p-" (
                set %%i=%%k;!%%i!
            ) else if "%%j"=="l" (
                set %%i=%~dp1%%k
            ) else if "%%j"=="lp" (
                set %%i=!%%i!;%~dp1%%k
            ) else if "%%j"=="lp-" (
                set %%i=%~dp1%%k;!%%i!
            ) else if "%%j"=="u" (
                set %%i=
            ) else if "%%j"=="z" (
                goto end
            ) else if "%%j"=="*" (
                if "%%k"=="*" (
                    echo recursive * hasn't been implemented, yet
                ) else if exist "%~dp1%%k\*" (
                    if exist "%~dp1%%k\%~snx1" (
                        call "%~dpnx0" "%~dp1%%k\%~snx1"
                    ) else (
                        rem echo dir ignored: %%k
                    )
                ) else if exist "%~dp1%%k" (
                    call "%~dpnx0" "%~dp1%%k"
                ) else (
                    rem echo file ignored: %%k
                )
            )
        )
    )

:end
