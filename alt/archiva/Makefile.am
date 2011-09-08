include libauto/coolauto.am

do_subst += \
    -e 's,[@]jettyhomedir[@],$(jettyhomedir),g' \
    -e 's,[@]jettyconfdir[@],$(jettyconfdir),g' \
    -e 's,[@]appdatadir[@],$(appdatadir),g' \
    -e 's,[@]appconfdir[@],$(appconfdir),g'

jettyhomedir    = ${datadir}/jetty
jettyconfdir    = ${sysconfdir}/jetty
appdatadir      = /var/lib/jetty
appconfdir      = ${appdatadir}/conf

dist_jettyhome_DATA = \
    archiva.xml

dist_jettyconf_DATA = \
    archiva-override-web.xml \
    archiva-jetty-config.xml

dist_appconf_DATA = \
    security.properties

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS} \
    archiva-jetty-config.xml

