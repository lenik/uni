@echo off

    set _FILES=%1
    shift

    set _ICOPT=
:icopt_loop
    if "%1"=="" goto icopt_end
    set _ICOPT=%_ICOPT% %1
    shift
    goto icopt_loop
:icopt_end

    if "%_ICOPT%"=="" (
        echo iconvs file-pattern iconv-options...
        goto clean
    )

    set _ICTMP=%TMP%
    if "%_ICTMP%"=="" set _ICTMP=%TEMP%
    if "%_ICTMP%"=="" set _ICTMP=/tmp
    set _ICDIFF=fc

    set _ICTOTAL=0
    set _ICWRITE=0

    echo [iconvs] iconv-files:   [%_FILES%]
    echo [iconvs] iconv-options: [%_ICOPT%]
    echo [iconvs] iconv-tempdir: [%_ICTMP%]
    echo [iconvs] iconv-diff:    [%_ICDIFF%]

    for %%i in (%_FILES%) do (
        echo [iconvs] %%i
        iconv %_ICOPT% "%%i" >%_ICTMP%\tmp
        %_ICDIFF% "%%i" "%_ICTMP%\tmp" >nul
        if errorlevel 1 (
            echo [iconvs] writing %%i
            copy /y "%_ICTMP%\tmp" "%%i" >nul
            set /a _ICWRITE = _ICWRITE + 1
        )
        set /a _ICTOTAL = _ICTOTAL + 1
    )

    echo [iconvs] total %_ICTOTAL% files
    echo [iconvs] modified %_ICWRITE% files

:clean
    set _FILES=
    set _ICOPT=
    set _ICTMP=
    set _ICDIFF=
    set _ICTOTAL=
    set _ICWRITE=
