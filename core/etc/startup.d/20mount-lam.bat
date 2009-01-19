rem #Mount LAM Modules

    setlocal

    set _lams=%HOME%\etc\lams
    if not exist "%_lams%" exit /b

    set i=0
    for /f "tokens=1,2 usebackq" %%i in ("%_lams%") do (
        if not exist "%%j" (
            rem pgd not exist
        ) else (
            set /a i = i + 1
            echo Mount LAM[!i!] %%i...
            call mount.pgd "%%j" "%%i"
        )
    )
