include libauto/project.am

incdir = ${includedir}/coolmake

dist_bin_SCRIPTS = \
    main

dist_man_MANS = \
    main.1

dist_inc_DATA = \
    m2proj.mf

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${pkgdatadir}/dist-util
	cp -a dist-util ${DESTDIR}${pkgdatadir}/

