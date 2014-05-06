include libauto/project.am

do_subst += \
    -e 's,[@]jettydir[@],$(jettydir),g' \
    -e 's,[@]jettylibdir[@],$(jettylibdir),g' \
    -e 's,[@]jettyconfdir[@],$(jettyconfdir),g' \
    -e 's,[@]appdatadir[@],$(appdatadir),g' \
    -e 's,[@]archivadir[@],$(archivadir),g'

jettydir        = ${datadir}/jetty8
jettylibdir	= ${jettydir}/lib
jettyconfdir    = ${sysconfdir}/jetty8
contextsdir	= ${jettyconfdir}/contexts
appdatadir     	= /var/lib/jetty8
archivadir      = ${jettydir}/.m2

dist_jettyconf_DATA = \
    archiva-db.xml

dist_pkgdata_DATA = \
    web.xml

dist_contexts_DATA = \
    contexts/archiva.xml

dist_archiva_DATA = \
    archiva.xml

#war_DATA = \
#    apache-archiva-2.war

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}
