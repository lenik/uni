include libauto/project.am

libjsdir = ${datadir}/javascript
jsdir = ${libjsdir}/jquery-focuspoint

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}


install-data-hook:
	mkdir -p ${DESTDIR}${jsdir}
	cp -a jquery-focuspoint/* ${DESTDIR}${jsdir}/
	find ${DESTDIR}${jsdir} -type f -exec chmod -x {} +
