rem #Mounting home(~) directory on A:

    if exist "a:" (
        subst | grep -q ^^A:
        rem 0 if A: is mapped, 1 if A: is floppy.
        if errorlevel 1 exit /b 1
        set INITDIR=a:
    )

    if not exist "a:" (
        subst a: "%HOME%"
        set INITDIR=a:
    )

    if not "%INITDIR%"=="" (
        echo INITDIR = %INITDIR% >%LAPIOTA%\proc\.env.as
    )

    exit /b 0
