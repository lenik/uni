include libauto/project.am

libjsdir = ${datadir}/javascript
jsdir = ${libjsdir}/jquery-parsley

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${jsdir}
	for d in Parsley.js-*/; do \
	    cp -a $$d/* ${DESTDIR}${jsdir}/; \
	done

