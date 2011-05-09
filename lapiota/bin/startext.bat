@echo off
REM $Id$

REM Find the start-button container
lc /nologo user32::FindWindowA('Shell_TrayWnd', 0)
set HWND_SHELL_TRAYWND=%ERRORLEVEL%

REM Find the start-button
lc /nologo user32::FindWindowExA(%HWND_SHELL_TRAYWND%, 0, 'Button', 0)
set HWND_START=%ERRORLEVEL%

REM Set the start-text
lc /nologo user32::SendMessageA(%HWND_START%, 12, 0, %1)
