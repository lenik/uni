include libauto/coolauto.am

dist_bin_SCRIPTS = \
    symltrace

dist_man_MANS = \
    symltrace.1

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
