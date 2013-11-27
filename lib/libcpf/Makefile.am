include libauto/coolauto.am

includedir = ${prefix}/include

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

install-data-hook:
	mkdir ${DESTDIR}${includedir}
	cp -R include/cpf ${DESTDIR}${includedir}
