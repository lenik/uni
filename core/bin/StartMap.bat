@echo off

:loop
if "%1"=="" goto end

if "%1"=="Z" goto Map_Z
if "%1"=="Y" goto Map_Y
if "%1"=="X" goto Map_X
if "%1"=="W" goto Map_W
if "%1"=="V" goto Map_V
if "%1"=="U" goto Map_U
if "%1"=="T" goto Map_T
if "%1"=="S" goto Map_S
if "%1"=="R" goto Map_R
if "%1"=="Q" goto Map_Q
if "%1"=="P" goto Map_P
if "%1"=="O" goto Map_O
if "%1"=="N" goto Map_N
if "%1"=="M" goto Map_M
if "%1"=="L" goto Map_L
if "%1"=="K" goto Map_K
if "%1"=="J" goto Map_J
if "%1"=="I" goto Map_I
if "%1"=="H" goto Map_H
if "%1"=="G" goto Map_G
if "%1"=="F" goto Map_F
if "%1"=="E" goto Map_E
if "%1"=="D" goto Map_D
if "%1"=="C" goto Map_C
if "%1"=="B" goto Map_B
if "%1"=="A" goto Map_A

:Map_Z
	Subst z: c:\
	goto m_next
:Map_Y
:Map_X
	goto m_next
:Map_R
	Subst r: c:\s\r\r
	goto m_next

:Map_A
:Map_B
:Map_C
:Map_D
:Map_E
:Map_F
:Map_G
:Map_H
:Map_I
:Map_J
:Map_K
:Map_L
:Map_M
:Map_N
:Map_O
:Map_P
:Map_Q
:Map_S
:Map_T
:Map_U
:Map_V
:Map_W
	goto m_next

:m_next
	shift
	goto loop

:end
