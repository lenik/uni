@echo off

echo Distribute cvs-initials to managed directory
echo Version 1
echo Author by Danci.Z

if "%1"=="" goto help

pushd "%1"
set count=0
for /d /r %%i in (. *) do (
	if not "%%i"=="CVS" (
		pushd %%i
		if not exist ".cvsignore" (
			set /a count=count+1
			echo %%i/
			echo *.bak>>.cvsignore
			echo *.tmp>>.cvsignore
			echo *.old>>.cvsignore
			echo *.$*>>.cvsignore
			echo *.~*>>.cvsignore
			echo ~*>>.cvsignore
			echo .private>>.cvsignore
			echo .local>>.cvsignore
			echo *.obj>>.cvsignore
			echo Thumbs.db>>.cvsignore
		)
		popd
	)
)
popd

echo.
echo Total %count% entries initialized.
set count=
goto end

:help
echo Syntax:
echo     dist-cvsi dir-name

:end
