include libauto/project.am

dist_bin_SCRIPTS = \
    src/bin/zhspeak \
    src/bin/zhspeak.py

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p "${DESTDIR}${pkgdatadir}"
	cp -R "${srcdir}"/src/zhspeak-data "${DESTDIR}${pkgdatadir}/data"

