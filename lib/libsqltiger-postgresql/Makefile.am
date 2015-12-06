include libauto/project.am

libsqldir = ${prefix}/lib/sql

do_subst += \
    -e 's,@libsqldir@,${libsqldir},g'

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${libsqldir}
	cp -a ${srcdir}/great ${DESTDIR}${libsqldir}
