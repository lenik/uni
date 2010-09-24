include libauto/coolauto.am

sitesdir = ${sysconfdir}/apache2/sites-available

dist_sites_DATA = \
    bodz-trac.site

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
