@rem <<:end_of_batch >/dev/null 2>/dev/null
@echo off

    setlocal
    set CYGWIN=nodosfilewarning

:lapiota_init
    call %~dp0lapiota-init init
    if errorlevel 1 (
        echo lapiota-init failed.
        echo maybe your lapiota is corrupted?
        exit /b 1
    )

    if not exist "%HOME%\." md "%HOME%"

    rem for Cygwin-1.7
    rem if this install program fails, the fstab may be cleared.
    umount -U
    mount "%CYGWIN_ROOT%\bin" /usr/bin
    mount "%CYGWIN_ROOT%\lib" /usr/lib
    mount "%LAPIOTA%" /lapiota

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
    if exist "%LAPIOTA%\local\bin\cmdw.exe" (
        call kopy "%LAPIOTA%\local\bin\cmdw.exe"
    )
    if exist "%LAPIOTA%\local\bin\cmd.exe" (
        kopy %LAPIOTA%\local\bin\cmd.exe
        rem unexpected
    )
    exit /b

:end_of_batch
