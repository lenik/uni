include libauto/project.am

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

install-data-hook:
	cd orig && make install DESTDIR="$(DESTDIR)" PREFIX="$(prefix)"
