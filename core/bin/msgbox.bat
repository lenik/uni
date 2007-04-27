@echo off

    setlocal

    set text=General MessageBox
    set title=DIRT
    set mb=64

    if not "%~1"=="" set text=%~1
    if not "%~2"=="" set title=%~2

:loop
    if "%3"=="" goto prep
    call libstr lc "%~3"
    if "%_ret%"=="ok"           set /a m1=m1 + 1
    if "%_ret%"=="cancel"       set /a m1=m1 + 2
    if "%_ret%"=="yes"          set /a m1=m1 + 4
    if "%_ret%"=="no"           set /a m1=m1 + 8
    if "%_ret%"=="abort"        set /a m1=m1 + 16
    if "%_ret%"=="retry"        set /a m1=m1 + 32
    if "%_ret%"=="ignore"       set /a m1=m1 + 64
    if "%_ret%"=="x"            set /a m2=m2 + 16
    if "%_ret%"=="?"            set /a m2=m2 + 32
    if "%_ret%"=="/"            set /a m2=m2 + 48
    if "%_ret%"=="i"            set /a m2=m2 + 64
    if "%_ret%"=="modal"        set /a m2=m2 + 4096
    shift
    goto loop

:prep
    rem ok default
    set mb=0

    rem ok cancel
    if "%m1%"=="2"              set mb=1
    if "%m1%"=="3"              set mb=1

    rem abort retry ignore
    if "%m1%"=="16"             set mb=2
    if "%m1%"=="64"             set mb=2
    if "%m1%"=="112"            set mb=2

    rem yes no
    if "%m1%"=="4"              set mb=4
    if "%m1%"=="8"              set mb=4
    if "%m1%"=="12"             set mb=4

    rem yes no cancel
    if "%m1%"=="6"              set mb=3
    if "%m1%"=="10"             set mb=3
    if "%m1%"=="14"             set mb=3

    rem retry cancel
    if "%m1%"=="32"             set mb=5
    if "%m1%"=="34"             set mb=5

    set /a mb = mb + m2

:begin
    lc /nologo user32::MessageBoxA(0, '%text%', '%title%', %mb%)
