rem #Start Home/Startups

    start 360safe

    REM start procexp
        set s_nam=procexp-root
        sc create %s_nam% binpath= "cmd /c start procexp" type= own type= interact >nul
        sc start %s_nam% >nul
        REM sc delete %s_nam% >nul

    start bwmonitor
    start juggler
    start lingoes
    start emule
