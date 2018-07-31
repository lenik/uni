include libauto/project.am

dist_bin_SCRIPTS = \
    zhspeak

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p "${DESTDIR}${pkgdatadir}"
	cp -RT "${srcdir}"/src "${DESTDIR}${pkgdatadir}"

