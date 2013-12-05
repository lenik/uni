include libauto/project.am

dist_bin_SCRIPTS = \
    vcscmd \
    vcscmd-ignore \
    vcsdirs

dist_aliases_DATA = \
    50vcscmds.sh

dist_completion_DATA = \
    vcscmds

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    ${setup_SCRIPTS} \
    vcscmd \
    vcscmd-ignore \
    vcsdirs

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"

install-data-hook:
	ln -snf vcscmd $(DESTDIR)$(bindir)/vcs
