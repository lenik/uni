include ../libauto/coolauto.am

mateconf2dir   = ${sysconfdir}/mateconf/2
mateconflibdir = /var/lib/mateconf
cooldefdir  = ${mateconflibdir}/mate-pref-xjl.defaults

dist_cooldef_DATA = \
    %mateconf-tree.xml

CLEANFILES = \
    %mateconf-tree.xml

%mateconf-tree.xml:
	echo Merge mateconf tree...
	rm -f $@
	mateconf-merge-tree $(srcdir)
