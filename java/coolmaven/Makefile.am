include libauto/coolauto.am

SUBDIRS = bash_completion.d assembly

bin_PROGRAMS = \
    get-common-prefix

dist_aliases_DATA = \
    coolmaven.sh

dist_bin_SCRIPTS = \
    coolmvn \
    edit2 \
    epom \
    git-m2ver \
    newmod \
    m2graph \
    m2help \
    m2installfile \
    m2layout \
    m2localdeps \
    m2localgraph \
    m2modlist \
    m2release \
    m2rpn \
    m2search \
    m2which \
    mjava \
    pom-cascade \
    pomid \
    pomtree

bin_SCRIPTS = \
    jetty \
    m2search-nexus \
    newmp

dist_shlib_SCRIPTS = \
    loadpom

dist_sysconf_DATA = \
    m2deploy.cfg \
    jettydefaults.xml

dist_man_MANS = \
    m2deploy.cfg.5

dist_pkgdata_DATA = \
    ng2txt.xsl \
    archetypes

#archetype-catalog.xml: Makefile
#	mvn -Dcatalog=$@ archetype:crawl

#archetypes: archetype-catalog.xml Makefile
#	xmltran -f "//archetype:=concat(groupId, ':', artifactId, ':', version, ':', normalize-space(translate(description, '&#10;', ' ')), '&#10;')" \
#		$(srcdir)/archetype-catalog.xml >$@

EXTRA_DIST = \
    jetty.in \
    m2search-nexus.in

CLEANFILES = \
    jetty \
    m2graph \
    m2help \
    m2installfile \
    m2layout \
    m2localdeps \
    m2localgraph \
    m2release \
    m2rpn \
    m2search \
    m2search-nexus \
    m2which \
    newmod \
    newmp \
    pom-cascade \
    pomid \
    pomtree
#    archetypes

install-data-hook:
	cd $(DESTDIR)$(bindir); \
	ln -snf m2installfile m2deployfile
