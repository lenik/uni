include libauto/project.am

dist_bin_SCRIPTS = \
    usecode

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	$(LN_S) -f usecode $(DESTDIR)$(bindir)/u
