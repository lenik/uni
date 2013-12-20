include libauto/project.am

includedir = ${prefix}/include

SUBDIRS = src test

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${includedir}
	cp -R ${srcdir}/include/* ${DESTDIR}${includedir}
