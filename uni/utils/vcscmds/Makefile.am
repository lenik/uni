include libauto/coolauto.am

dist_bin_SCRIPTS = \
    vcscmd \
    vcscmd-ignore

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

install-data-hook:
	ln -snf vcscmd $(DESTDIR)$(bindir)/vcs
