@echo off
rem $Id: dist-cvsi.bat,v 1.5 2006-04-23 05:40:16 lenik Exp $

:version
    echo Distribute cvs-initials to managed directory
    echo Version 1
    echo Author by Danci.Z

:begin
    REM if "%1"=="" goto help

    if exist .cvsignore (
        copy /y .cvsignore %TEMP%\CVSIGNOR >nul
    ) else (
        copy /y "%~dp0cvsignor.def" %TEMP%\CVSIGNOR >nul
    )

:scan
    set /a count=count+1
    echo ./
    copy %TEMP%\CVSIGNOR .\.cvsignore >nul

    for /d /r %%i in (*) do (
	if not "%%~nxi"=="CVS" (
            set /a count=count+1
	    echo %%i/
            copy "%TEMP%\CVSIGNOR" "%%i\.cvsignore" >nul
        )
    )

:report
    echo.
    echo Total %count% entries initialized.
    set count=
    goto end

:help
    echo Syntax:
    echo     dist-cvsi

:end
