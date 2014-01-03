include libauto/project.am

includedir = ${prefix}/include

SUBDIRS = src test

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

dist-hook:
	cp -R ${srcdir}/include ${distdir}/include
	cd ${distdir}/include; find -name '*.h' -o -type f -delete

install-data-hook:
	mkdir -p ${DESTDIR}${includedir}
	cp -R ${srcdir}/include/* ${DESTDIR}${includedir}
