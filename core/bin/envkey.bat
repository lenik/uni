@echo off

    if "%1"=="clear" (
	echo clear...
        set ALLUSERSPROFILE=
        set APPDATA=
        set CLIENTNAME=
        set CommonProgramFiles=
        set COMPUTERNAME=
        set HOME=
        set HOMEDRIVE=
        set HOMEPATH=
        set LOGONSERVER=
        set ProgramFiles=
        set SESSIONNAME=
        set SystemDrive=
        set SystemRoot=
        set TMPDIR=
        set USERDOMAIN=
        set USERNAME=
        set USERPROFILE=
        set Basemake=
        set Bkoffice=
        set CATALINA_BASE=
        set CATALINA_HOME=
        set CLASSPATH=
        set ComSpec=
        set DIR_T_HOME=
        set DRIVERNETWORKS=
        set DRIVERWORKS=
        set GTK_BASEPATH=
        set INCLUDE=
        set INETSDK=
        set JAVA_HOME=
        set LIB=
        set MSSdk=
        set Mstools=
        set NUMBER_OF_PROCESSORS=
        set NUT_SUFFIXED_SEARCHING=
        set NUTSUFFIX=
        set OS=
        set PERLLIB=
        set PROCESSOR_ARCHITECTURE=
        set PROCESSOR_IDENTIFIER=
        set PROCESSOR_LEVEL=
        set PROCESSOR_REVISION=
        set PYTHONPATH=
        set RATL_RTHOME=
        set SYBASE=
        set VS71COMNTOOLS=
        set VTOOLSD=
        set windir=
        goto end
    )

    if "%1"=="" (
        for /f "delims=*" %%i in ('set') do (
            call cmdex leftof "%%i" "="
            echo !_ret!
        )
        goto end
    )


:arg_loop
    for /f "delims=*" %%i in ('set %1') do (
        call cmdex leftof "%%i" "="
        echo !_ret!
    )
    shift
    if not "%1"=="" goto arg_loop
    goto end


:end
