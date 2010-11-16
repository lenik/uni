include libauto/coolauto.am

SUBDIRS = gconf

x11confdir  = ${sysconfdir}/X11
xsessiondir = ${x11confdir}/Xsession.d

gconf2dir   = ${sysconfdir}/gconf/2

bgdir       = ${datadir}/backgrounds

x11conf_DATA = \
    Xmodmap

dist_bg_DATA = \
    wallpapers/Boheme.jpg \
    wallpapers/Pointy.jpg

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
    load-xmodmap \
    gconf-cleanbase/%gconf-tree.xml

%: %.in Makefile
	$(do_subst) \
        -e 's,[@]x11confdir[@],$(x11confdir),g' \
        -e 's,[@]xsessiondir[@],$(xsessiondir),g' \
        -e 's,[@]gconf2dir[@],$(gconf2dir),g' \
        <$< >$@

path.diff: path.orig path
	-diff -u ${srcdir}/path.orig ${srcdir}/path >$@

path.orig: ${gconf2dir}/path
	cp -f "$<" "$@"

