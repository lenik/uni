@Echo Off

	Cls
	Echo 远程登录......
	If "%1"=="" Goto Help
        Set %1=True

	If Not "%2"=="" Set BD=%2:

        Goto %1

:Win32
        Call Z:\H\L\InModule Set
        Goto StartShell

:WinNT
	Title 桃壳信业 位于 中国浙江余杭开发部 的 主机
        Call Z:\H\L\InModule Set
        If "%USERNAME%"=="谢继雷" Goto MirrorDisk
        If "%USERNAME%"=="dansei" Goto MirrorDisk
        If "%USERNAME%"=="Administrator" Goto MirrorDisk

	Goto CMirrorDisk
		:MirrorDisk
                Call Z:\H\L\InModule Mirror
		Echo   [桃壳软件] 虚拟命令行模式 的 映像驱动器 被 启动
		:CMirrorDisk

	Color 2F
	Rem 启动Telnet服务器
		Echo   [桃壳软件] 启动Telnet安全扩展
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
		Echo   [桃壳软件] Telnet安全扩展 被 启动
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
	Echo   [桃壳信业] UNIX仿真环境 被 启动
	Echo   [桃壳信业] 操作历史记录 被 启动
	Echo -------------------------------------------------------
        Echo   欢迎您访问 桃壳信业 位于 中国浙江余杭开发部 的 主机

:End

:Exit
