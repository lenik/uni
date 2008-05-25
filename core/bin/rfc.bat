@echo off

    setlocal
    set rfc_home=%LAPIOTA%\usr\ref\rfc
    set rfc_edit=metapad

    if "%~1"=="u" (
        set rfc_edit=ue
        shift
    )

    set rfc_name=%~1
    if "%rfc_name%"=="" goto help

    set rfc_n=%rfc_name%
    :trim0
    if "%rfc_n:~0,1%"=="0" (
        set rfc_n=%rfc_n:~1%
        goto trim0
    )
    call libstr sn "%rfc_n%"

    if "%_ret%"=="2" set rfc_p=00xx
    if "%_ret%"=="3" set rfc_p=0%rfc_n:~0,1%xx
    if "%_ret%"=="4" set rfc_p=%rfc_n:~0,2%xx

:start
    pushd "%rfc_home%" >nul

    if exist "%rfc_p%\%rfc_n%.*" (
        for %%i in (%rfc_p%\%rfc_n%.*) do (
            if "%%~xi"==".txt" (
                start %rfc_edit% "%%i"
            ) else (
                start "Loading..." "%%i"
            )
        )
        goto end
    )

:listing
    echo RFC-index::
    grep -i "%rfc_name%" rfc-index.txt

    if /i not "%0"=="rfc" pause

    goto end

:help
    echo rfc [index]
    goto end

:cleanup
    popd >nul
    set rfc_home=
    set rfc_n=
    set rfc_p=

:end
