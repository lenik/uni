@Echo Off

	Cls
	Echo Զ�̵�¼......
	If "%1"=="" Goto Help
        Set %1=True

	If Not "%2"=="" Set BD=%2:

        Goto %1

:Win32
        Call Z:\H\L\InModule Set
        Goto StartShell

:WinNT
	Title �ҿ���ҵ λ�� �й��㽭�ຼ������ �� ����
        Call Z:\H\L\InModule Set
        If "%USERNAME%"=="л����" Goto MirrorDisk
        If "%USERNAME%"=="dansei" Goto MirrorDisk
        If "%USERNAME%"=="Administrator" Goto MirrorDisk

	Goto CMirrorDisk
		:MirrorDisk
                Call Z:\H\L\InModule Mirror
		Echo   [�ҿ����] ����������ģʽ �� ӳ�������� �� ����
		:CMirrorDisk

	Color 2F
	Rem ����Telnet������
		Echo   [�ҿ����] ����Telnet��ȫ��չ
		Goto TL2
		If Not "%TelnetStatus%"=="" Goto TL1
		If "%TelnetStatus%"=="Installed" Goto TL2
			Echo 4 >Z:\TEMP0000.$$$
			Echo 0 >>Z:\TEMP0000.$$$
			tlntadmn <Z:\TEMP0000.$$$ >Nul
			Del /F/Q Z:\TEMP0000.$$$
			Set TelnetStatus=Installed
		:TL1
			Set TelnetStatus=ToInstall
		:TL2
		Echo   [�ҿ����] Telnet��ȫ��չ �� ����
        Goto StartShell

:Help
	Echo StartSys <"Win32" or "WinNT"> [<Boot Disk>]
	Echo.
	Echo E.g. StartSys WinNT Z:

	Goto Exit

:StartShell
	Goto Shell%1

:ShellWin32

        A -H -R Z:\H\L\Command
        Copy /Y Z:\H\L\Command Z:\Command.com >Nul
        A +H +R Z:\H\L\Command

	Goto CShell

:ShellWinNT

	Copy /Y Z:\WinNT\System32\Command.com Z:\Command.com >Nul
	Set Comspec=Z:\Command.com

	Goto CShell

:CShell
	Echo   [�ҿ���ҵ] UNIX���滷�� �� ����
	Echo   [�ҿ���ҵ] ������ʷ��¼ �� ����
	Echo -------------------------------------------------------
        Echo   ��ӭ������ �ҿ���ҵ λ�� �й��㽭�ຼ������ �� ����

:End

:Exit
