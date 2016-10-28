include libauto/project.am

localdir = ${prefix}

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${localdir}
	cp -a dist/* ${DESTDIR}${localdir}/

