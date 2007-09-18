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
    umount /
    umount /usr/bin
    umount /usr/lib
    umount /lapiota
    umount /tmp
    mount -c /mnt
    mount %CYGWIN_ROOT% /
    mount %LAPIOTA% /lapiota
    mount %CYGWIN_ROOT%\bin /usr/bin
    mount %CYGWIN_ROOT%\lib /usr/lib
    mount "%TEMP%" /tmp

    rem Always using the cygwin/perl
    bash -c '/lapiota/etc/install %*'

:end
    exit /b
:end_of_batch
