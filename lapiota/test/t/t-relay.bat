@echo on

    setlocal

    if "%~1"=="" (
        set port=00%random%
        set port=%port:~-2%
    ) else (
        set port=%~1
    )
    set /a tport=1000 + port

    start "Target server at port %port%" recv %port%

    start "Relay server at port %tport%" relay -v localhost %port%

    title Source Client: Connect to the relay server at %tport%

    send localhost %tport%
