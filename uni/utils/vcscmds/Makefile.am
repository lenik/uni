include libauto/coolauto.am

dist_bin_SCRIPTS = \
    vcs

dist_aliases_DATA = \
    vcscmds.sh

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
