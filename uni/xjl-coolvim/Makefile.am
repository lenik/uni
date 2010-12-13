include libauto/coolauto.am

vimconfdir = ${sysconfdir}/vim

dist_bin_SCRIPTS = \
    coolvim \
    guesstab \
    ifedit

dist_vimconf_DATA = \
	vimrc.cool

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

do_subst2 = $(do_subst) -e 's,[@]vimconfdir[@],$(vimconfdir),g'

%: %.in Makefile
	$(do_subst2) <$< >$@
	chmod -w "$@"
