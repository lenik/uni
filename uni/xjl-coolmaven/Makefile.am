include libauto/coolauto.am

dist_bin_SCRIPTS = \
    newmod \
    jetty \
    m2search

bin_SCRIPTS = \
    m2search-nexus

dist_pkgdata_DATA = \
    ng2txt.xsl

EXTRA_DIST = m2search-nexus.in
CLEANFILES = m2search-nexus

%: %.in Makefile
	$(do_subst) <$< >$@
