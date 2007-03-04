@echo off

    setlocal

    rem -d DIR   - directory for output files
    rem -ff      - output fields before methods (fieldsfirst)
    rem -lnc     - output original line numbers as comments (lnc)
    rem -nl      - split strings on newline characters (splitstr)
    rem -o       - overwrite output files without confirmation
    rem -r       - restore package directory structure
    rem -s EXT   - output file extension (default: .jad)
    set _opt=-ff -lnc -nl -o -r -s .java

    if "%1"=="--jad" (
        set _opt=%~2
        shift
        shift
    )

    if "%1"=="" (
        echo jads [--jad JAD_OPTIONS] bin_dir src_dir
        goto end
    )

    set _bin=%~1
    set _src=%~1
    if not "%2"=="" set _src=%~2

    if not exist "%_src%\." md "%_src%"

    pushd "%_bin%" >nul
    for /r /d %%i in (*) do (
        if exist "%%i\*.class" (
            jad -^& %_opt% -d "..\%_src%" "%%i\*.class"
        )
    )
    popd >nul
