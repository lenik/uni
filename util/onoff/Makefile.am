include libauto/project.am

dist_bin_SCRIPTS = \
    onoff \
    onoff-img

dist_man_MANS = \
    onoff.1 \
    onoff-img.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${pkgdatadir}
	cp -R ${srcdir}/mapping ${DESTDIR}${pkgdatadir}/mapping
	cd ${DESTDIR}/${bindir}; ln -snf onoff on
	cd ${DESTDIR}/${bindir}; ln -snf onoff off
