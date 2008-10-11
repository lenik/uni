rem #Mount LAM Modules

    setlocal

    set _lams=%HOME%\etc\lams
    if not exist "%_lams%" exit /b

    for /f "tokens=1,2 usebackq" %%i in ("%_lams%") do (
        if not "%%j"=="" (
            echo Mount LAM %%i...
            call mount.pgd "%%j" "%%i"
        )
    )
