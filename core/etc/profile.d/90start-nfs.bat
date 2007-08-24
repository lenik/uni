rem #Start NFS for working console

    call nfs --verbose --test-only /x >%TEMP%\nfs-x
    for /f "delims=" %%i in ('grep auto_mkdir %TEMP%\nfs-x') do (
        set _SCRATCH=%%i
    )
    rem e.g. M:/scratch/2007/2007-04/2007-04-08
    set _SCRATCH=%_SCRATCH:/=\%
    if exist "%_SCRATCH%\." (
       subst w: "%_SCRATCH:~11,-11%"
       w:
       cd "\%_SCRATCH:~-10%"
       start "NFS Working Console - %_SCRATCH:~-10%"
    )
