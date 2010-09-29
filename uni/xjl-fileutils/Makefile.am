include libauto/coolauto.am

dist_bin_SCRIPTS = \
    textp \
    binaryp \
    swap \
    enfile \
    symfl \
    symflatten \
    rename-p

dist_man_MANS = \
    symfl.1 \
    rename-p.1

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
	postinst.in \
	prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@

install-data-hook:
	cd $(DESTDIR)$(bindir); \
		$(LN_S) enfile disfile
