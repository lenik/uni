include libauto/project.am

libjsdir = ${datadir}/javascript
jsdir = ${libjsdir}/datatables

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${jsdir}; \
	${srcdir}/restore ${DESTDIR}${jsdir}

