@echo off

    set _VAROJ=C:\.radiko\varoj
    set _CIRK=C:\.cirkonstancoj
    set _DIRT=C:\t
    set _PF=%ProgramFiles%
    set _MIAJ=C:\.radiko\.miaj

echo  Probing mapping-drives.bat
title Mapping drives...
    if exist "%_MIAJ%\drive\mapping-drives.bat" (
        cd /d "%_MIAJ%\drive"
        call mapping-drives.bat
    )

echo  Probing reset-junctions.bat
title Reset Junctions...
    if exist "%_MIAJ%\reset-junctions.bat" (
        cd /d "%_MIAJ%"
        call reset-junctions.bat
    )

echo  Probing mounting...
title Mounting...
    if exist "%_MIAJ%\image\*" (
        cd /d "%_MIAJ%\image"
        for %%i in (*) do (
            if "%%~ni"=="crit-mirror" "%%i"
            if "%%~ni"=="crit-cvs" "%%i"
            if "%%~ni"=="pack-misc-box" "%%i"
        )
    )

echo  Probing system tools...
title Start System Tools...
    if exist "%_DIRT%\3\Taskmgr.exe" (
        start %_DIRT%\3\Taskmgr.exe
    )

echo  Probing IM...
title Starting IM...
    if exist "%_VAROJ%\Network\Messenger\QQ\QQ.exe" (
        cd /d "%_VAROJ%\Network\Messenger\QQ"
        start QQ.exe
    )

    if exist "%_PF%\MSN Messenger\msnmsgr.exe" (
        cd /d "%_PF%\MSN Messenger"
        start msnmsgr.exe
    )

    if exist "%_PF%\Gaim\gaim.exe" (
        cd /d "%_PF%\Gaim"
        start gaim.exe
    )

echo  Probing XDict...
title Starting XDict...
    if exist "%_VAROJ%\Help\Kingsoft PowerWord 2005\xdict.exe" (
        cd /d "%_VAROJ%\Help\Kingsoft PowerWord 2005"
        start xdict.exe
    )

echo  Probing P2P Downloader...
title Starting P2P Downloader...
    if exist "%_PF%\eMule\emule.exe" (
        cd /d "%_PF%\eMule"
        start emule.exe
    )

echo  Probing Apache...
title Start Apache...
    if exist "%_CIRK%\Perl\Apache2\bin\ApacheMonitor.exe" (
        cd /d "%_CIRK%\Perl\Apache2\bin"
        call dCall iisreset /stop
        if errorlevel 30 set _iisrestart=1
        net start Apache2
        start ApacheMonitor.exe
    )
    if "%_iisrestart%"=="1" (
        iisreset /start
    )

echo  Probing NFS for Today...
title NFS for Today...
    rem call nfs

echo  Probing NFS for working console
title Launch working console
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

echo  Probing Browser...
title Start Browser...
    if exist "%_PF%\Maxthon\Maxthon.exe" (
        cd /d "%_PF%\Maxthon"
        start Maxthon.exe
    )

echo  Probing Emacs...
    if exist "%_DIRT%\0\EM.bat" (
        title Emacs-console
        cls
        mode con cols=67 lines=10
        echo      ::: :::           ::
        echo       :   :             :
        echo       :   :             :
        echo       :   :   :::::     :     :::::   :::::  ::: :    :::::
        echo       : : :  :     :    :    :     : :     :  : : :  :     :
        echo       : : :  :::::::    :    :       :     :  : : :  :::::::
        echo       : : :  :          :    :       :     :  : : :  :
        echo        : :   :     :    :    :     : :     :  : : :  :     :
        echo        : :    :::::   :::::   :::::   :::::  :: : ::  :::::
        wm -hide Emacs-console
        call "%_DIRT%\0\EM.bat"
    )
