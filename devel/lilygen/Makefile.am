include libauto/project.am

templatedir = ${pkgdatadir}/template

do_subst += -e 's,@templatedir@,${templatedir},g'

dist_bin_SCRIPTS = \
    mkentity

man_MANS = \
    mkentity.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${templatedir}
	cp -R ${srcdir}/template/* ${DESTDIR}${templatedir}
