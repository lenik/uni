include libauto/coolauto.am

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in \
    repos-plugins \
    repos-web

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"

install-data-hook:
	mkdir -p "$(DESTDIR)$(pkgdatadir)"
	cp -R $(srcdir)/{repos-plugins,repos-web} "$(DESTDIR)$(pkgdatadir)"
