@echo off

    call findabc bind
    set named=%_home%\named.exe
    set conf=%home%\etc\named.conf

    echo BIND named: %named%
    echo Using conf: %conf%

    sc create named binpath= "%named% -c %conf%"

    call findabc httpd-2
    set httpd=%_home%\bin\httpd.exe
    set conf=%LAPIOTA%\etc\httpd.conf
    if not exist "%conf%" set conf=%_home%\conf\httpd.conf
    REM sc create httpd binpath= "%httpd% -k runservice"
    "%httpd%" -f "%conf%" -k install -n httpd
