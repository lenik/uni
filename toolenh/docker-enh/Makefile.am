include libauto/project.am

dist_bin_SCRIPTS = \
    docker-gc \
    registry-push

dist_man_MANS = \
    docker-gc.1 \
    registry-push.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	cp -a $(srcdir)/inc $(DESTDIR)${prefix}/include

