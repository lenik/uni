include libauto/project.am

libjsdir = ${datadir}/javascript
jsdir = ${libjsdir}/jquery-selectize

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${jsdir}
	cp -a selectize.js/* ${DESTDIR}${jsdir}/
