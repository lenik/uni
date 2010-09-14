
include libauto/coolauto.am

dist_bin_SCRIPTS = \
	repl \
	wcc \
	unindent \
	iconvf

setupdir = ${datadir}/setup/${PACKAGE_NAME}

setup_SCRIPTS = \
    post-install.sh \
    pre-remove.sh

EXTRA_DIST = \
	post-install.sh.in \
	pre-remove.sh.in

CLEANFILES = \
	post-install.sh \
	pre-remove.sh

%: %.in Makefile
	$(do_subst) <$< >$@
