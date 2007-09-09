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

    rem Mounting...

    rem Always using the cygwin/perl
    bash -c '/lapiota/etc/install %*'

:end
    exit /b
:end_of_batch
