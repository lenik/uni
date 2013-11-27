include libauto/coolauto.am

includedir = ${prefix}/include

SUBDIRS = src test

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${includedir}
	cp -R ${srcdir}/include/* ${DESTDIR}${includedir}
