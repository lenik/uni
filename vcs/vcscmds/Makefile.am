include libauto/project.am

dist_bin_SCRIPTS = \
    vcscmd \
    vcscmd-ignore \
    vcsdirs

dist_alias_DATA = \
    50vcscmds.sh

dist_completion_DATA = \
    vcscmds

dist_man_MANS = \
    vcscmd.1 \
    vcscmd-ignore.1 \
    vcsdirs.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_setup_SCRIPTS}

install-data-hook:
	ln -snf vcscmd $(DESTDIR)$(bindir)/vcs
