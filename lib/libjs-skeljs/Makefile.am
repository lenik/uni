include libauto/project.am

mkincdir = ${includedir}/coolmake
libjsdir = ${datadir}/javascript
jsdir = ${libjsdir}/skeljs

dist_mkinc_HEADERS = \
    include/skel.mk

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${jsdir}
	cp -a \
            package.json css/ img/ js/ \
            ${DESTDIR}${jsdir}

