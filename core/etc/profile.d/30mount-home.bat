rem #Mounting home(~) directory on A:

    if exist "A:" (
        subst | grep -q ^^A:
        rem 0 if A: is mapped, 1 if A: is floppy.
        if errorlevel 1 exit /b 1
        set INITDIR=A:
    )

    if not exist "A:" (
        subst A: "%HOME%"
        set INITDIR=A:
    )

    if not "%INITDIR%"=="" (
        echo INITDIR = %INITDIR% >%LAPIOTA%\proc\.env.as
    )

    exit /b 0
