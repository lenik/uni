@echo off

    setlocal

    REM SPACE in IMage name IS UNSUPPORTED.

    if "%~1"=="" (
        echo syntax: cput ^<image-name^>
        echo return the cpu-time ^(duration^) in exit code
        goto end
    )

    set im=%~1

:get_t1
    tasklist /v | grep -i %im% > %TEMP%\res1
    for /f "tokens=1-10," %%i in (%TEMP%\res1) do (
        if "%%i"=="%im%" (
            set pid=%%j
            set sess=%%k
            set sid=%%l
            set mem=%%m
            set memu=%%n
            set stat=%%o
            set user=%%p
            set cput=%%q
            set cap=%%r
        )
    )
    set t1_hour=%cput:~,-6%
    set t1_min=%cput:~-5,-3%
    set t1_sec=%cput:~-2%
    set /a t1 = t1_hour * 3600 + t1_min * 60 + t1_sec
    REM echo t1=%t1%

:get_t0
    set t0=0
    for /f %%i in (%temp%\%im%.cput) do (
        set t0=%%i
    )
    echo %t1% >"%temp%\%im%.cput"
    REM echo t0=%t0%

    set /a cput = t1 - t0

:ret
    err %cput%

:end
