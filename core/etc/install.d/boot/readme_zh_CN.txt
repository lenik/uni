
Lapiota 安装指南

简介
====

    Lapiota 是一个免费工具集，其中包括我写的 Lapiota 核心工具集和收集的工具系列。

    核心工具集用于维护 Lapiota 本身和其它主要用于辅助开发的日用工具。

    收集的工具系列中的每个工具都精心审查过其中的版权协议，以确定可以免费使用并且允许再发布。

    Lapiota 由若干个 LAM 模块组成，Lapiota 核心工具集提供了对 LAM 的支持。LAM 的形式可以是目录，或磁盘映象，目前主要以 TrueCrypt/NTFS 的映像形式发布。因为每个 LAM 模块包含了大量的文件（特别是小文件），通过建立整个目录的映像可以大大减少复制、安装时间，省去了压缩/解压缩的过程。另外，LAM 模块中对部分文件使用了 NTFS 压缩，进一步节省存储空间，进一步降低复制、网络传输时间。

    标准 Lapiota 由以下几个 LAM 组成：

    lam.sys     Lapiota 底层系统，包括 cygwin-1.7.0 和 msys-1.0.11
    lam.kala    Lapiota 核心工具集和常用工具
    lam.lang    若干开发工具和语言，包括 LISP/Scheme/ML系列语言，Scilab/Maxima，R 等

    其中的依赖关系是，lam.sys 构成一个类 Unix 系统（以下简称*nix）的模拟层，Lapiota 中使用的很多工具如 perl/python 等，用的是 Cygwin 的版本而不是 ActivePerl 等 Win32 编译版，比如文件 /usr/bin/perl 而不是 C:\Program Files\ActivePerl\...。Lapiota 核心工具集包括 Win32 和 *nix 两种不同形式的工具，其中 *nix 部分依赖于模拟的文件系统，基本上无需编译即可运行于 *nix 上。所以 lam.kala 依赖于 lam.sys。其它 LAM 模块如 lam.lang，lam.mobi 等一般都依赖于 lam.sys 和 lam.kala。也就是说 lam.sys+lam.kala 构成一个基本的 Lapiota 系统。

    而 lam.sys 和 lam.kala 都是以 LAM 映像形式发布的，操作系统并不支持 LAM，而提供对 LAM 支持的 lam.kala 是封装在 lam.kala 之内的，而为了运行 lam.kala，首先要装配 lam.sys 和 lam.kala 模块，而装配这些模块的工具又在 lam.kala 里面，那么怎么办呢？于是需要一个 Lapiota 的引导程序，由这个引导程序装配 lam.sys 和 lam.kala，然后调用 lam.kala 中的程序启动 Lapiota。

    这个引导程序包括以下几个部分：

    TrueCrypt 提供了 NTFS 映像支持，可用于把 foo.tc 装配到 X: 上，然后 Win32 的 mountvol 工具可用于把 X: 挂载到某个目录如 C:\foo 上。不过 TrueCrypt 主要用于文件系统加密，并不是为了做磁盘映像而设计的，*nix 提供了对磁盘映象的直接支持（通过 Loopback 设备），而 Win32 则不支持。因此采用 TrueCrypt 的关键目的是克服 Win32 的不足。

    Lapiota 的文件系统结构如：（区分大小写）
        LAM 根目录 （建议 C:\lam ）
            image/      映像文件存放位置 C:\lam\image
            boot/       引导系统
                truecrypt/  TrueCrypt 工具
                fstab.tc    装配表
            home/       用户目录（不同于 C:\Documents and Settings\xxxx，这个用户目录相当于 *nix 的 ~/ 目录）
            kala/       lam.kala 挂载点
            sys/        lam.sys 挂载点
            lang/       lam.lang 挂载点

    一个 Win32 启动脚本 lapiota-boot.bat 根据装配表 lam/boot/fstab.tc 装配 TrueCrypt 映像，并调用 lam.kala 中的启动程序：

        lam/kala/etc/startup.d/ 下的所有程序（按字母顺序）。
        $HOME/etc/startup.d/    下的所有程序（按字母顺序）。


安装
====

    首先建立 Lapiota 的文件系统结构：

        > mkdir c:\lam
        > mkdir c:\lam\image
        > mkdir c:\lam\boot
        > mkdir c:\lam\home
        > mkdir c:\lam\kala
        > mkdir c:\lam\sys
        > mkdir c:\lam\lang

    复制 LAM 映像到 c:\lam\image，解压 boot.zip 到 c:\lam\boot，修改 fstab.tc 装配表，其中表中每一行的结构为：
        盘符 映像文件 挂载点

    盘符必须为系统未使用的盘符，推荐 R:=lam.kala，S:=lam.sys，L:=lam.lang。对于标准不存在的映像文件，引导程序会自动忽略。

    每次修改 fstab.tc 表，还需修改 truecrypt/Favorite Volumes.xml 文件，编辑 Favorite Volumes.xml 使对应记录和 fstab.tc 中的一致。其中 Favorite Volumes.xml 中不含挂载点。

    运行 boot/mount.bat，当提示密码时输入 LaM。装配成功后检查 C:\lam\kala 和 C:\lam\sys，应该能看到挂载的目录。如果没有，可能是你的 C: 不是 NTFS 文件系统。Lapiota 不支持 FAT-32 等 NTSFS 以外的文件系统。

    打开控制台（运行 cmd），进入 C:\lam\kala\bin，设置必要的环境参数，运行 lapiota-install.bat：

        > chdir /d c:\lam\kala\bin
        > set HOME=c:\lam\home
        > set LAM_ROOT=c:\lam
        > set CYGWIN_ROOT=c:\lam\sys\cygwin-1.7
        > set MSYS_ROOT=c:\lam\sys\msys-1.0.11
        > set LAPIOTA=c:\lam\kala
        > set LAM_KALA=/lam/kala
        > lapiota-install

    接着检查 开始菜单/程序/启动，其中增加的 lapiota-boot 用于引导 Lapiota。重新启动后，应该能看到引导窗口。引导程序等待映像文件被装载，装载完成后按任意键开始启动 Lapiota。Lapiota 成功启动后按 <Win>-<F11> 应该能看到关于对话框，或者按 <Win>-<C> 打开控制台（更多快捷键请参考 lam/kala/INSTALL）。至此，安装完毕。

    有任何疑问和建议请联系我。


谢继雷 <xjl@99jsj.com>
2009.8
