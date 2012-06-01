include libauto/coolauto.am

SUBDIRS = mateconf

do_subst += \
    -e 's,[@]mateconf2dir[@],$(mateconf2dir),g'

mateconf2dir   = ${sysconfdir}/mateconf/2

setup_SCRIPTS = \
    postinst \
    prerm

setup_DATA = \
    path.diff

path.diff: path.0 path
	-diff -u ${srcdir}/path.0 ${srcdir}/path >$@

#path.0: ${mateconf2dir}/path
#	cp -f "$<" "$@"

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS} \
    mateconf-cleanbase/%mateconf-tree.xml
