
include libauto/subst.am

sysconfdir = /etc
aliasesdir = ${sysconfdir}/bash_aliases.d

dist_bin_SCRIPTS = \
    verexec

dist_aliases_DATA = \
    cooldeb.sh

vcmdlist = dh_make pwd

install-data-hook:
	cd $(DESTDIR)$(bindir); \
        for f in $(vcmdlist); do \
            ln -sf verexec $${f}_ver; \
        done

uninstall-hook:
	cd $(DESTDIR)$(bindir); \
        for f in $(vcmdlist); do \
            rm -f \$${f}_ver; \
        done
