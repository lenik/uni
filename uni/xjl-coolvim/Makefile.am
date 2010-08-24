
aliasesdir = /etc/bash_aliases.d
profilesdir = /etc/profile.d

dist_bin_SCRIPTS = \
    coolvim \
    guesstab \
    ifedit

dist_aliases_DATA = \
    aliases.d/coolvim.sh

dist_profiles_DATA = \
	profile.d/coolvim.sh

install-exec-hook:
	cd $(DESTDIR)$(bindir); ln -snf coolvim coolvims
