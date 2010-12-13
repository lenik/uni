include libauto/coolauto.am

dist_bin_SCRIPTS = \
    pdfmon \
    treetex \
    Fpst

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    postinst \
    prerm

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"
