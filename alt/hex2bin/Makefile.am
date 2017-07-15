include libauto/project.am

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${prefix}/bin
	mkdir -p ${DESTDIR}${prefix}/man/man1
	cd src; make install INSTALL_DIR=${DESTDIR}${prefix}

