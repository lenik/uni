include libauto/project.am

incdir = ${includedir}/coolmake

dist_bin_SCRIPTS = \
    main \
    m2href \
    m2mkdirs \
    m2mklib

dist_man_MANS = \
    main.1 \
    m2href.1 \
    m2mkdirs.1 \
    m2mklib.1

dist_inc_DATA = \
    m2proj.mf

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_setup_SCRIPTS}

install-debug:
	sudo ln -snf $$PWD/m2proj.mf $(DESTDIR)${incdir}

install-data-hook:
	mkdir -p ${DESTDIR}${pkgdatadir}/dist-util
	cp -a dist-util ${DESTDIR}${pkgdatadir}/

