include libauto/coolauto.am

dist_bin_SCRIPTS = \
    coolvim \
    guesstab \
    ifedit

dist_aliases_DATA = \
    aliases.d/coolvim.sh

dist_profile_DATA = \
	profile.d/coolvim.sh

dist_sudoers_DATA = \
	sudoers.d/env_editor

setup_SCRIPTS = \
	postinst \
	prerm

EXTRA_DATA = \
	postinst.in \
	prerm.in

CLEANFILES = ${setup_SCRIPTS}

install-data-hook:
	cd $(DESTDIR)$(bindir); ln -snf coolvim coolvims

%: %.in Makefile
	$(do_subst) <$< >$@
