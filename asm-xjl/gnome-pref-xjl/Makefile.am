include libauto/coolauto.am

SUBDIRS = gconf

do_subst += \
    -e 's,[@]gconf2dir[@],$(gconf2dir),g'

gconf2dir   = ${sysconfdir}/gconf/2

setup_SCRIPTS = \
    postinst \
    prerm

setup_DATA = \
    path.diff

path.diff: path.0 path
	-diff -u ${srcdir}/path.0 ${srcdir}/path >$@

#path.0: ${gconf2dir}/path
#	cp -f "$<" "$@"

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS} \
    gconf-cleanbase/%gconf-tree.xml

