include libauto/project.am

dist_bin_SCRIPTS = \
    locmore \
    sourcelines

dist_man_MANS = \
    locmore.1 \
    sourcelines.1

dist_data_DATA = \
    license.txt

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

debug:
	sudo ln -snf $$PWD/sourcelines.in $(DESTDIR)${bindir}/sourcelines
#	sudo ln -snf $$PWD/locmore.in $(DESTDIR)${bindir}/locmore

