include libauto/project.am

subst_scripts = \
    vcscmd \
    vcscmd-ignore \
    vcsdirs

dist_bin_SCRIPTS = \
    $(subst_scripts) \
    vcsroot

dist_alias_DATA = \
    50vcscmds.sh

dist_completion_DATA = \
    vcscmds

dist_man_MANS = \
    vcscmd.1 \
    vcsdirs.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${subst_scripts} \
    ${dist_setup_SCRIPTS}

install-debug:
	sudo ln -snf $$PWD/vcsdirs.in /bin/vcsdirs
	sudo ln -snf $$PWD/vcscmd.in /bin/vcscmd

install-data-hook:
	ln -snf vcscmd $(DESTDIR)$(bindir)/vcs

