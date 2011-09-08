include libauto/coolauto.am

do_subst += \
    -e 's,[@]jettyhomedir[@],$(jettyhomedir),g' \
    -e 's,[@]jettyconfdir[@],$(jettyconfdir),g'

jettyhomedir    = ${datadir}/jetty
jettyconfdir    = $(sysconfdir)/jetty
appconfdir      = /var/lib/jetty/conf

dist_jettyhome_DATA = \
    archiva.xml

dist_jettyconf_DATA = \
    archiva-override-web.xml \
    archiva-jetty-config.xml

dist_jetty_DATA = \
    archiva.xml

dist_appconf_DATA = \
    security.properties

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in \
    archiva-jetty-config.xml.in

CLEANFILES = ${setup_SCRIPTS} \
    archiva-jetty-config.xml

