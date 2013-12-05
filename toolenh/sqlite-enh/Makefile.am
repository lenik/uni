include libauto/project.am

dist_bin_SCRIPTS = \
    sqlite-dump

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

install-data-hook:
	ln -snf sqlite-dump $(DESTDIR)${bindir}/sqlite3-dump
