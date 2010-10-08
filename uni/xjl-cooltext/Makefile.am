include libauto/coolauto.am

dist_bin_SCRIPTS = \
    repl \
    wcc \
    unindent \
    iconvf \
    grepstat

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRITPS}

%: %.in Makefile
	$(do_subst) <$< >$@
