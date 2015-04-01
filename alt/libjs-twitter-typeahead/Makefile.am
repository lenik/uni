include libauto/project.am

libjsdir = ${datadir}/javascript
jsdir = ${libjsdir}/twitter-typeahead

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${jsdir}
	cp -a typeahead.js/* ${DESTDIR}${jsdir}/

