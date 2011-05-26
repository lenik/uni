@echo off
rem $Id$


    set _copy_for_retry=%0 %*
    set _cvsroot=%CVSROOT%
    set _cvsopts=
    set _cvscmdopts=-r HEAD
    set _writable=
    set _module=CVSROOT
    set _modbase=
    set _newbase=

    if "%1"=="" goto help

:args

    if "%1"=="" goto args_x

    if /i "%1" equ "-d" (
        set _cvsroot=%2
        shift
        shift
        goto args
    )

    if /i "%1" equ "-m" (
        set _module=%2
        shift
        shift
        goto args
    )

    if /i "%1" equ "-n" (
        set _newbase=%2
        shift
        shift
        goto args
    )

    if /i "%1" equ "-o" (
        set _cvsopts=%~2
        shift
        shift
        goto args
    )

    if /i "%1" equ "-c" (
        set _cvscmdopts=%~2
        shift
        shift
        goto args
    )

    if /i "%1" equ "-w" (
        set _writable=1
        shift
        goto args
    )

    echo invalid argument "%1"
    goto end

:args_x

    if "%_cvsroot"=="" (
        echo CVSROOT is not specified.
        goto end
    )

    if "%_module"=="" (
        echo CVS Module is not specified.
        goto end
    )


rem 1, cvs-export

    set _tempdir=cvscopy.%random%
    mkdir %_tempdir%
    cd %_tempdir%

    rem ./_tempdir/ *module/base*

    echo.
    echo.
    echo [CMD] cvs %_cvsopts% -d %_cvsroot% export %_cvscmdopts% %_module%
    cvs %_cvsopts% -d %_cvsroot% export %_cvscmdopts% %_module%

    if not exist "%_module%\*" (
        echo CVS export failed.
        goto end
    )


rem 2, "chroot" - move */module/base  to */base

    rem get the basename '%_module%'

    for /d %%i in ("%_module%") do set _modbase=%%~nxi
    if "%_newbase%"=="" set _newbase=%_modbase%
    if "%_newbase%"=="" (
        echo can't get basename from "%_module%"
    )

    rem only copy newer files, and overwrite Readonly files

    rem copy ./*module/base*/ to ./../*base-only*/
    echo.
    echo.
    echo [CMD] xcopy /y /d /e /r "%_module%" ..\%_newbase%\
    xcopy /y /d /e /r "%_module%" ..\%_newbase%\

    cd..

    echo.
    echo.
    echo [CMD] rd /s /q %_tempdir% (PWD: %CD%)
    rd /s /q %_tempdir%

    rem make readonly, because it's a mirror-copy, should not be changed.
    if not "%_writable%"=="1" (
        echo.
        echo.
        echo [CMD] attrib /s +r %_newbase%\*
        attrib /s +r %_newbase%\*
    )

rem 3, update .sync file

    set _syncfile=.sync-cvscopy-%_modbase%-%_newbase%.bat

    echo.
    echo.
    echo [MAK] build sync script: %_syncfile%

    rem if not exist .VEX\sync\* md .VEX\sync
    echo @echo off >%_syncfile%
    echo rem CVSCOPY-Rev: $Revision: 1.12 $ >>%_syncfile%
    echo rem Thie file is auto generated by CVSCOPY Program, and it will >>%_syncfile%
    echo rem be automaticly updated. Please don't edit this file. >>%_syncfile%
    echo %_copy_for_retry% >>%_syncfile%

    goto end


:help
    echo [CVSCOPY] CVS Local Copy and Synchronize
    echo Written by Snima Denik, Update $Date: 2006-04-23 05:38:01 $
    echo Syntax
    echo     cvscopy
    echo       -d ... (cvsroot)
    echo       -o ... (cvs options)
    echo       -c ... (cvs command options, e.g. -r rev/tag.. )
    echo       -w (writable-checkout)
    echo       -m ... (module) [ /(path) ]
    echo       -n (basename-rename)
    echo Example
    echo    cvscopy -d :pserver:dansei@q:/crit/active/usnap -o -z3
    echo            -m bodzjava/bj-commons -n BJCommons
    goto end

:end
    set _copy_for_retry=
    set _cvsroot=
    set _cvsopts=
    set _cvscmdopts=
    set _writable=
    set _module=
    set _modbase=
    set _newbase=
    set _tempdir=