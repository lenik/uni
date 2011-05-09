include libauto/coolauto.am

dist_bin_SCRIPTS = \
    a2logtail \
    a2reload \
    a2restart \
    a2start \
    a2stop

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
