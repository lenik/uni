@echo off

echo [GET] %1
	call getweb www.mp4cn.com %1 >_get1

echo [PAS] /music/.*/[0-9]+.html
	getmed -k -r /music/.*/[0-9]+.html >_link1
	del _get1

echo [LST] Level 1
	set /a index=0
	for /f %%i in (_link1) do (
		echo [GET] %%i
		call getweb www.mp4cn.com %%i >L1_!index!.html
		set /a index=!index!+1
	)
	del _link1

echo [PAS] Indirect /downmp3/
	getmed -k -r=/downmp3/ >_link2
	del L1_*

echo [LST] Level 2
	set /a index=0
	for /f %%i in (_link2) do (
		md album_!index!
		echo [GET] %%i
		call getweb www.mp4cn.com http://www.mp4cn.com%%i >album_!index!\lst
		set /a index=!index!+1
	)
	del _link2

echo [PAS] Level 2 Decrypt
	repl -r "-c=c-text::-d $file" lst

echo [PAS] Get Albums
	set _get_rt=%cd%
	echo @echo off >%_get_rt%\all-downs.bat
	echo set path=c:\progra~1\flashget;%%path%% >>%_get_rt%\all-downs.bat

	for /d /r %%i in (*) do (
		pushd %%i >nul
		echo [MAK] %%i
		getmed -k >_downs_.txt
		type _downs_.txt >>%_get_rt%\all-downs.txt

		echo. >>%_get_rt%\all-downs.bat
		for /f %%f in (_downs_.txt) do (
			echo flashget "%%f" "%%i" >>%_get_rt%\all-downs.bat
		)
		del lst
		popd >nul
	)
