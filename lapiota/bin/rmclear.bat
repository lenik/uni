@echo off

rmevents -i "%~1" -o "%~1.tmp" -e nul
if not exist "%~1.tmp" (
	echo failed to clear events
	goto end
)

move "%~1" "%~1.bak"
move "%~1.tmp" "%~1"

:end
