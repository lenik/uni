
include libauto/subst.am

sysconfdir = /etc
aliasesdir = ${sysconfdir}/bash_aliases.d

dist_bin_SCRIPTS = \
    verexec \
	dir-ver \
	dh_make-autover \
	dh_debname \
	deb-redist \
	dwhich

dist_aliases_DATA = \
    cooldeb.sh

dist_man_MANS = \
	dwhich.1

vcmdlist = dh_make pwd

install-data-hook:
	cd $(DESTDIR)$(bindir); \
        for f in $(vcmdlist); do \
            ln -sf verexec $${f}_ver; \
        done; \
		ln -sf dir-ver dir-nover

uninstall-hook:
	cd $(DESTDIR)$(bindir); \
        for f in $(vcmdlist); do \
            rm -f \$${f}_ver; \
        done; \
		rm -f dir-nover
