include libauto/coolauto.am

dist_bin_SCRIPTS = \
    usecode

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    postinst \
    prerm

install-data-hook:
	$(LN_S) -f usecode $(DESTDIR)$(bindir)/u
