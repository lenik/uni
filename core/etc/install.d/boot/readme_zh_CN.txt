
Lapiota ��װָ��

���
====

    Lapiota ��һ����ѹ��߼������а�����д�� Lapiota ���Ĺ��߼����ռ��Ĺ���ϵ�С�

    ���Ĺ��߼�����ά�� Lapiota �����������Ҫ���ڸ������������ù��ߡ�

    �ռ��Ĺ���ϵ���е�ÿ�����߶������������еİ�ȨЭ�飬��ȷ���������ʹ�ò��������ٷ�����

    Lapiota �����ɸ� LAM ģ����ɣ�Lapiota ���Ĺ��߼��ṩ�˶� LAM ��֧�֡�LAM ����ʽ������Ŀ¼�������ӳ��Ŀǰ��Ҫ�� TrueCrypt/NTFS ��ӳ����ʽ��������Ϊÿ�� LAM ģ������˴������ļ����ر���С�ļ�����ͨ����������Ŀ¼��ӳ����Դ����ٸ��ơ���װʱ�䣬ʡȥ��ѹ��/��ѹ���Ĺ��̡����⣬LAM ģ���жԲ����ļ�ʹ���� NTFS ѹ������һ����ʡ�洢�ռ䣬��һ�����͸��ơ����紫��ʱ�䡣

    ��׼ Lapiota �����¼��� LAM ��ɣ�

    lam.sys     Lapiota �ײ�ϵͳ������ cygwin-1.7.0 �� msys-1.0.11
    lam.kala    Lapiota ���Ĺ��߼��ͳ��ù���
    lam.lang    ���ɿ������ߺ����ԣ����� LISP/Scheme/MLϵ�����ԣ�Scilab/Maxima��R ��

    ���е�������ϵ�ǣ�lam.sys ����һ���� Unix ϵͳ�����¼��*nix����ģ��㣬Lapiota ��ʹ�õĺܶ๤���� perl/python �ȣ��õ��� Cygwin �İ汾������ ActivePerl �� Win32 ����棬�����ļ� /usr/bin/perl ������ C:\Program Files\ActivePerl\...��Lapiota ���Ĺ��߼����� Win32 �� *nix ���ֲ�ͬ��ʽ�Ĺ��ߣ����� *nix ����������ģ����ļ�ϵͳ��������������뼴�������� *nix �ϡ����� lam.kala ������ lam.sys������ LAM ģ���� lam.lang��lam.mobi ��һ�㶼������ lam.sys �� lam.kala��Ҳ����˵ lam.sys+lam.kala ����һ�������� Lapiota ϵͳ��

    �� lam.sys �� lam.kala ������ LAM ӳ����ʽ�����ģ�����ϵͳ����֧�� LAM�����ṩ�� LAM ֧�ֵ� lam.kala �Ƿ�װ�� lam.kala ֮�ڵģ���Ϊ������ lam.kala������Ҫװ�� lam.sys �� lam.kala ģ�飬��װ����Щģ��Ĺ������� lam.kala ���棬��ô��ô���أ�������Ҫһ�� Lapiota �����������������������װ�� lam.sys �� lam.kala��Ȼ����� lam.kala �еĳ������� Lapiota��

    �����������������¼������֣�

    TrueCrypt �ṩ�� NTFS ӳ��֧�֣������ڰ� foo.tc װ�䵽 X: �ϣ�Ȼ�� Win32 �� mountvol ���߿����ڰ� X: ���ص�ĳ��Ŀ¼�� C:\foo �ϡ����� TrueCrypt ��Ҫ�����ļ�ϵͳ���ܣ�������Ϊ��������ӳ�����Ƶģ�*nix �ṩ�˶Դ���ӳ���ֱ��֧�֣�ͨ�� Loopback �豸������ Win32 ��֧�֡���˲��� TrueCrypt �Ĺؼ�Ŀ���ǿ˷� Win32 �Ĳ��㡣

    Lapiota ���ļ�ϵͳ�ṹ�磺�����ִ�Сд��
        LAM ��Ŀ¼ ������ C:\lam ��
            image/      ӳ���ļ����λ�� C:\lam\image
            boot/       ����ϵͳ
                truecrypt/  TrueCrypt ����
                fstab.tc    װ���
            home/       �û�Ŀ¼����ͬ�� C:\Documents and Settings\xxxx������û�Ŀ¼�൱�� *nix �� ~/ Ŀ¼��
            kala/       lam.kala ���ص�
            sys/        lam.sys ���ص�
            lang/       lam.lang ���ص�

    һ�� Win32 �����ű� lapiota-boot.bat ����װ��� lam/boot/fstab.tc װ�� TrueCrypt ӳ�񣬲����� lam.kala �е���������

        lam/kala/etc/startup.d/ �µ����г��򣨰���ĸ˳�򣩡�
        $HOME/etc/startup.d/    �µ����г��򣨰���ĸ˳�򣩡�


��װ
====

    ���Ƚ��� Lapiota ���ļ�ϵͳ�ṹ��

        > mkdir c:\lam
        > mkdir c:\lam\image
        > mkdir c:\lam\boot
        > mkdir c:\lam\home
        > mkdir c:\lam\kala
        > mkdir c:\lam\sys
        > mkdir c:\lam\lang

    ���� LAM ӳ�� c:\lam\image����ѹ boot.zip �� c:\lam\boot���޸� fstab.tc װ������б���ÿһ�еĽṹΪ��
        �̷� ӳ���ļ� ���ص�

    �̷�����Ϊϵͳδʹ�õ��̷����Ƽ� R:=lam.kala��S:=lam.sys��L:=lam.lang�����ڱ�׼�����ڵ�ӳ���ļ�������������Զ����ԡ�

    ÿ���޸� fstab.tc �������޸� truecrypt/Favorite Volumes.xml �ļ����༭ Favorite Volumes.xml ʹ��Ӧ��¼�� fstab.tc �е�һ�¡����� Favorite Volumes.xml �в������ص㡣

    ���� boot/mount.bat������ʾ����ʱ���� LaM��װ��ɹ����� C:\lam\kala �� C:\lam\sys��Ӧ���ܿ������ص�Ŀ¼�����û�У���������� C: ���� NTFS �ļ�ϵͳ��Lapiota ��֧�� FAT-32 �� NTSFS ������ļ�ϵͳ��

    �򿪿���̨������ cmd�������� C:\lam\kala\bin�����ñ�Ҫ�Ļ������������� lapiota-install.bat��

        > chdir /d c:\lam\kala\bin
        > set HOME=c:\lam\home
        > set LAM_ROOT=c:\lam
        > set CYGWIN_ROOT=c:\lam\sys\cygwin-1.7
        > set MSYS_ROOT=c:\lam\sys\msys-1.0.11
        > set LAPIOTA=c:\lam\kala
        > set LAM_KALA=/lam/kala
        > lapiota-install

    ���ż�� ��ʼ�˵�/����/�������������ӵ� lapiota-boot �������� Lapiota������������Ӧ���ܿ����������ڡ���������ȴ�ӳ���ļ���װ�أ�װ����ɺ��������ʼ���� Lapiota��Lapiota �ɹ������� <Win>-<F11> Ӧ���ܿ������ڶԻ��򣬻��߰� <Win>-<C> �򿪿���̨�������ݼ���ο� lam/kala/INSTALL�������ˣ���װ��ϡ�

    ���κ����ʺͽ�������ϵ�ҡ�


л���� <xjl@99jsj.com>
2009.8
