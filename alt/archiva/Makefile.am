include libauto/project.am

do_subst += \
    -e 's,[@]jettyhomedir[@],$(jettyhomedir),g' \
    -e 's,[@]jettyconfdir[@],$(jettyconfdir),g' \
    -e 's,[@]appdatadir[@],$(appdatadir),g' \
    -e 's,[@]appconfdir[@],$(appconfdir),g' \
    -e 's,[@]archivaconfdir[@],$(archivaconfdir),g'

jettyhomedir    = ${datadir}/jetty
jettyconfdir    = ${sysconfdir}/jetty
appdatadir      = /var/lib/jetty
appconfdir      = ${appdatadir}/conf
archivaconfdir  = ${jettyhomedir}/.m2

dist_archivaconf_DATA = \
    archiva.xml

dist_jettyconf_DATA = \
    archiva-override-web.xml \
    archiva-jetty-config.xml

dist_appconf_DATA = \
    security.properties

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    archiva-jetty-config.xml \
    ${dist_setup_SCRIPTS}
