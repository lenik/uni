include libauto/coolauto.am

dist_aliases_DATA = \
    coolmaven.sh

dist_bin_SCRIPTS = \
    newmod \
    jetty \
    m2help \
    m2search \
    m2graph

bin_SCRIPTS = \
    m2search-nexus \
    newmp

dist_pkgdata_DATA = \
    ng2txt.xsl \
    archetypes

archetypes: archetype-catalog.xml Makefile
	xmltran -f "//archetype:=concat(groupId, ':', artifactId, ':', version, ':', normalize-space(translate(description, '&#10;', ' ')), '&#10;')" \
		$(srcdir)/archetype-catalog.xml >$@

EXTRA_DIST = m2search-nexus.in
CLEANFILES = m2search-nexus archetypes

%: %.in Makefile
	$(do_subst) <$< >$@
