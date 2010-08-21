
aliasesdir = /etc/bash_aliases.d

dist_bin_SCRIPTS = \
    coolvim \
    guesstab \
    ifedit

dist_aliases_DATA = \
    aliases.d/coolvim.sh

install-exec-hook:
	cd $(DESTDIR)$(bindir); ln -snf coolvim coolvims
