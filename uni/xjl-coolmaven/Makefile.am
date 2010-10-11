include libauto/coolauto.am

SUBDIRS = bash_completion.d

bin_PROGRAMS = \
    get-common-prefix

dist_aliases_DATA = \
    coolmaven.sh

dist_bin_SCRIPTS = \
    newmod \
    jetty \
    m2help \
    m2search \
    m2graph \
    pomid \
    m2installfile

bin_SCRIPTS = \
    m2search-nexus \
    newmp

dist_sysconf_DATA = \
    m2deploy.cfg

dist_man_MANS = \
    m2deploy.cfg.5

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

install-data-hook:
	cd $(DESTDIR)$(bindir); \
        ln -snf m2installfile m2deployfile
