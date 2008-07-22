@echo off

    setlocal

    call jsub -Xx.bat -Ddelimiter="|" -Dfile-field=name -Scsv -Mgsp=.autofind.bat.gsp .autofind.csv
