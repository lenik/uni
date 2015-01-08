include libauto/project.am

libjsdir = ${datadir}/javascript
jsdir = ${libjsdir}/jquery-chosen

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${jsdir}
	for zip in chosen_v*.zip; do \
		unzip -d ${DESTDIR}${jsdir}/ $$zip; \
		break; \
	done
