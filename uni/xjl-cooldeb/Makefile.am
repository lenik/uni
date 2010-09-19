
include libauto/coolauto.am

dist_bin_SCRIPTS = \
    verexec \
	dir-ver \
	dh_make-autover \
	dh_debname \
	dwhich \
	newdeb \
	deb-redist \
	deb-clean \
	cideb \
	apt-get-reinstall2

dist_aliases_DATA = \
    cooldeb.sh

dist_man_MANS = \
	dwhich.1 \
	newdeb.1 \
	cideb.1

setup_SCRIPTS = \
	postinst \
	prerm

EXTRA_DIST = \
	postinst.in \
	prerm.in

CLEANFILES = \
	${setup_SCRIPTS}

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

%: %.in Makefile
	$(do_subst) <$< >$@
