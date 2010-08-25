
aliasesdir = /etc/bash_aliases.d
profilesdir = /etc/profile.d
sudoersdir = /etc/sudoers.d

dist_bin_SCRIPTS = \
    coolvim \
    guesstab \
    ifedit

dist_aliases_DATA = \
    aliases.d/coolvim.sh

dist_profiles_DATA = \
	profile.d/coolvim.sh

dist_sudoers_DATA = \
	sudoers.d/env_editor

install-data-hook:
	cd $(DESTDIR)$(bindir); ln -snf coolvim coolvims
	cd $(DESTDIR)$(sudoersdir); \
		chmod 440 env_editor
