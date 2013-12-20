include libauto/project.am

includedir = ${prefix}/include

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir ${DESTDIR}${includedir}
	cp -R include/cpf ${DESTDIR}${includedir}
