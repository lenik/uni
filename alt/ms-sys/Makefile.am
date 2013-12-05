include libauto/project.am

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"

install-data-hook:
	cd orig && make install DESTDIR="$(DESTDIR)" PREFIX="$(prefix)"
