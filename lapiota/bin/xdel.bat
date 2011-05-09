@echo off

    rem Remove files only appeared in %dst%

:prepare
    setlocal
    if "%1"=="" (
        goto help
    ) else if "%1"=="-v" (
        set _verbose=1
    ) else if "%1"=="-i" (
        set _ignore=%~2
        call libstr sn "%~2"
        set _nignore=!_ret!
        shift
    ) else (
        goto init
    )
    shift
    goto prepare

:init
    set _1=%~1
    set _2=%~2

    rem temporary drives could be created when pushd
        pushd "%_1%"
        if errorlevel 1 goto err
        for /d %%i in (.) do set _src=%%~dpnxi
        popd

        pushd "%_2%"
        if errorlevel 1 goto err
        for /d %%i in (.) do set _dst=%%~dpnxi
        popd

        for /d %%i in (.) do set _cur=%%~dpnxi

        pushd "%_1%"
        cd /d "%_cur%"
        pushd "%_2%"

    rem
        call libstr sn "%_src%"
        set _nsrc=%_ret%
        call libstr sn "%_dst%"
        set _ndst=%_ret%

        if "%_verbose%"=="1" echo src[%_nsrc%]=%_src%
        if "%_verbose%"=="1" echo dst[%_ndst%]=%_dst%

    goto dst_loop

:src_loop
    for /r "%_src%" %%i in (*) do (
        set i=%%i
        set i_=!i:~%_nsrc%!
        set j=%_dst%!i_!
        echo !i! : !j!
    )
    goto endloop

:dst_loop
    for /r "%_dst%" %%i in (*) do (
        set i=%%i
        if not "!_last_dpi!"=="%%~dpi" (
            echo [xdel] %%~dpi
            set _last_dpi=%%~dpi
        )
        set i_=!i:~%_ndst%!
        if not "%_ignore%"=="" (
            set _i_=!i_:~0,%_nignore%!
            if /i "!_i_!"=="%_ignore%" (
                if "%_verbose%"=="1" echo skipped !i!
                set _skip=1
                set /a sn = sn + 1
            )
        )
        if not "!_skip!"=="1" (
            set j=%_src%!i_!
            if not exist !j! (
                echo Remove !i!...
                del /f /a "!i!"
                set /a n = n + 1
            )
        )
        set _skip=0
    )
    goto endloop

:endloop
    echo Total %n% files removed, %sn% files skipped.

:clean_up
    popd
    popd
    goto end

:help
    echo xdel [-v] [-i ^<ignore-prefix^>] ^<primary-dir^> ^<secondary-dir^>
    goto end

:err_pop2
    popd
:err_pop1
    popd
:err
    echo Error occured: %ERRORLEVEL%

:end
