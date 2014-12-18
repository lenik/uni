include libauto/project.am

libjsdir = ${datadir}/javascript
jsdir = ${libjsdir}/jquery-inputmask

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${jsdir}
	for d in RobinHerbots-jquery.inputmask-*/; do \
	    cp -a $$d/* ${DESTDIR}${jsdir}/; \
	done
