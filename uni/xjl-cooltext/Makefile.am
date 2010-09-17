
include libauto/coolauto.am

dist_bin_SCRIPTS = \
	repl \
	wcc \
	unindent \
	iconvf

setupdir = ${datadir}/setup/${PACKAGE_NAME}

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
