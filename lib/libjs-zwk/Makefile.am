include libauto/project.am

libjsdir = ${datadir}/javascript
jsdir = ${libjsdir}/zwk

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${jsdir}
	cp -aT zwk/ ${DESTDIR}${jsdir}
