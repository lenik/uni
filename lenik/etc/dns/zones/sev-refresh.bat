@echo off
for %%i in (*) do (
    echo [sev] %%i
	sev -q %%i
)
