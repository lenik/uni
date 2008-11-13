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

:mounts
    if not exist "%HOME%\." md "%HOME%"

    rem Mount relocations...
        cd /d "%~dp0..\etc\install.d"
        rem reset cygwin /
        del "%CYGWIN_ROOT%\etc\fstab" >nul
        rem init msys, because it's in fstab.
        call findabc msys
        set MSYS_ROOT=%_home%
        make -Bf 00Makefile

:main
    rem Always using the cygwin/perl
    bash -c '/lapiota/etc/install %*'

    cd /d "%~dp0..\etc\install.d"
    call 10install-booter

    cd win32
    for %%f in (*.bat) do (
        echo %%f
        call %%f
    )

:end_refresh
    rem send quit to:
    rem     autohotkey
    rem     winevent
    rem     fastcmd
    echo Update system environment.
    wbcast Environment

:end_exec
    if exist "%LAPIOTA%\local\bin\cmdw.exe" (
        call kopy "%LAPIOTA%\local\bin\cmdw.exe"
    )
    if exist "%LAPIOTA%\local\bin\cmd.exe" (
        kopy %LAPIOTA%\local\bin\cmd.exe
        rem unexpected
    )
    exit /b
