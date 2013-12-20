include libauto/project.am

dist_setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    repos-plugins \
    repos-web

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p "$(DESTDIR)$(pkgdatadir)"
	cp -R $(srcdir)/{repos-plugins,repos-web} "$(DESTDIR)$(pkgdatadir)"
