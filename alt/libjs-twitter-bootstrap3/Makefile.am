include libauto/project.am

libjsdir = ${datadir}/javascript
jsdir = ${libjsdir}/twitter-bootstrap3

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${jsdir}; \
	for d in bootstrap-*-dist/; do \
	    cp -a $$d/* ${DESTDIR}${jsdir}/; \
	done

