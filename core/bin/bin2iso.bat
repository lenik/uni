@echo off

	if "%1"=="*" (
		for %%i in (*.cue) do (
			set base=%%~ni
			if exist "!base!.bin" (
				echo [bin2iso] !base!.bin
				call bin2iso "!base!"
			)
		)
		goto end
	)

	bchunk "%~1.bin" "%~1.cue" "%~1"

:end
