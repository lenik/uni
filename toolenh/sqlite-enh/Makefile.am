include libauto/project.am

dist_bin_SCRIPTS = \
    sqlite-dump

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

install-data-hook:
	ln -snf sqlite-dump $(DESTDIR)${bindir}/sqlite3-dump
