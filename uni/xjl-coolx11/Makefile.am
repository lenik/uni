include libauto/coolauto.am

x11confdir  = ${sysconfdir}/X11
xsessiondir = ${x11confdir}/Xsession.d

gconf2dir   = ${sysconfdir}/gconf/2
gconflibdir = /var/lib/gconf
cooldefdir  = ${gconflibdir}/xjl-coolx11.defaults

x11conf_DATA = \
    Xmodmap

dist_cooldef_DATA = \
    gconf-cleanbase/%gconf-tree.xml

dist_xsession_DATA = \
    load-xmodmap

setup_SCRIPTS = \
    postinst \
    prerm

setup_DATA = \
    path.diff

EXTRA_DIST = \
    postinst.in \
    prerm.in \
    load-xmodmap.in

CLEANFILES = ${setup_SCRIPTS} \
    load-xmodmap
    gconf-cleanbase/%gconf-tree.xml

%: %.in Makefile
	$(do_subst) \
        -e 's,[@]x11confdir[@],$(x11confdir),g' \
        -e 's,[@]xsessiondir[@],$(xsessiondir),g' \
        -e 's,[@]gconf2dir[@],$(gconf2dir),g' \
        -e 's,[@]cooldefdir[@],$(cooldefdir),g' \
        <$< >$@

path.diff: ${gconf2dir}/path path
	-diff -u ${gconf2dir}/path ${srcdir}/path >$@

gconf-cleanbase/%gconf-tree.xml:
	gconf-merge-tree $(srcdir)/gconf-cleanbase
