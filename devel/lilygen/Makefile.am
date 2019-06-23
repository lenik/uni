include libauto/project.am

templatedir = ${pkgdatadir}/template

do_subst += -e 's,@templatedir@,${templatedir},g'

dist_bin_SCRIPTS = \
    lilytool \
    mkentity \
    mkv-andr \
    modscan

man_MANS = \
    lilytool.1 \
    mkentity.1 \
    mkv-andr.1 \
    modscan.1

dist_alias_DATA = \
    lilygen.sh

dist_completion_DATA = \
    completions/lilytool

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${templatedir}
	cp -R ${srcdir}/template/* ${DESTDIR}${templatedir}
	cd ${DESTDIR}${completiondir}; \
	    ln -snf lilytool lt;

