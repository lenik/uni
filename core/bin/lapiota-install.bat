@rem <<:end_of_batch >/dev/null 2>/dev/null
@echo off

    setlocal

:lapiota_init
    call %~dp0lapiota-init init
    if errorlevel 1 (
        echo lapiota-init failed.
        echo maybe your lapiota is corrupted?
        exit /b 1
    )

    if not exist "%HOME%\." md "%HOME%"

    rem Mount relocations...
    cd /d "%~dp0..\etc\install.d"
    make -Bf 00Makefile

    rem Always using the cygwin/perl
    bash -c '/lapiota/etc/install %*'

    cd /d "%~dp0..\etc\install.d"
    call 10install-booter

    cd win32
    for %%f in (*.bat) do (
        echo %%f
        call %%f
    )

:end
    exit /b
:end_of_batch
