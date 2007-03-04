@echo off

    rem example:
    rem     dejar --jad "-s .myext" example.jar

    set _opt=-r -s .java
    if "%1"=="--jad" (
        set _opt=%~2
        shift
        shift
    )

    if "%1"=="" (
        echo normal mode:
        echo    dejar [--jad "jad-options"] [file-list...]
        echo batch mode:
        echo    dejar [--jad "jad-options"] *
        goto end
    )

    if "%1"=="*" (
        echo [batch] start
        set _gopt=%_opt%
        for %%i in (*.jar) do (
            set _base=%%i
            if /i not "!_base:~0,4!"=="src-" (
                echo [batch] dejar !_base!
                call dejar --jad "!_gopt!" "!_base!"
            )
        )
        set _gopt=
        echo [batch] end
        goto end
    )

    set _base=%~nx1

    pushd "%~dp1" >nul

    if exist "_bin_\." (
        echo [dejar] delete _bin_/
        rd /s /q _bin_
    )
    if exist "_src_\." (
        echo [dejar] delete _src_/
        rd /s /q _src_
    )

    md _bin_ 2>nul
    md _src_ 2>nul
    cd _bin_

    set _x=%temp%\djzi%random%
    call zipinfo "../%_base%" >%_x%
    for /f "delims=: tokens=1-4" %%i in (%_x%) do (
        set _jar_size=%%i
        set _jar_files=%%j
        set _jar_javas=%%k
        set _jar_classes=%%l
    )

    rem echo jarinfo: size=%_jar_size% files=%_jar_files% javas=%_jar_javas% classes=%_jar_classes%

    set _cap=[dejar] decompressing %_base%
    jar -xvf "../%_base%" | pc -t=%_jar_files% %_cap%
    cd..

    set _cap=[dejar] decompiling
    call jads _bin_ _src_ | grep Generating | pc -t=%_jar_classes% %_cap%

    set _cap=[dejar] archiving src-%_base% ...
    cd _src_
    jar -cvf "../src-%_base%" . | grep .java | pc -t=%_jar_classes% %_cap%

    echo [dejar] cleaning...
    cd ..
    rd /s /q _bin_ 2>nul
    rd /s /q _src_ 2>nul

    echo [dejar] done!

    popd >nul

    set _base=
    set _opt=

:end
