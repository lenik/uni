@echo off

    setlocal

    set _id=YES
    if not "%1"=="" (
        call libstr uc "%1"
        set _id=!_ret!
    )

    set _id=ID%_id%

    loop [ 1000 ] wm -c=#32770 -s="%_id%"
