include ../libauto/coolauto.am

gconf2dir   = ${sysconfdir}/gconf/2
gconflibdir = /var/lib/gconf
cooldefdir  = ${gconflibdir}/xjl-coolx11.defaults

dist_cooldef_DATA = \
    %gconf-tree.xml

CLEANFILES = \
    %gconf-tree.xml

%gconf-tree.xml:
	echo Merge gconf tree...
	rm -f $@
	gconf-merge-tree $(srcdir)
