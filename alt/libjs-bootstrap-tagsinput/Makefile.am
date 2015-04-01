include libauto/project.am

libjsdir = ${datadir}/javascript
jsdir = ${libjsdir}/bootstrap-tagsinput

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}


install-data-hook:
	mkdir -p ${DESTDIR}${jsdir}
	cp -a bootstrap-tagsinput/* ${DESTDIR}${jsdir}/
	find ${DESTDIR}${jsdir} -name '*.zip' -delete

