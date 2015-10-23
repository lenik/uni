include libauto/project.am

libsqldir = ${libdir}/sql

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${libsqldir}
	cp -a ${srcdir}/great ${DESTDIR}${libsqldir}
