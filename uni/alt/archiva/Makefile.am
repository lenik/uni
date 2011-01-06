include libauto/coolauto.am

do_subst += \
    -e 's,[@]jettydir[@],$(jettydir),g' \
    -e 's,[@]jettyconfdir[@],$(jettyconfdir),g'

jettydir        = ${datadir}/jetty
jettyconfdir    = $(sysconfdir)/jetty

jettyconf_DATA = \
    archiva-jetty-config.xml

dist_jettyconf_DATA = \
    archiva-override-web.xml

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in \
    archiva-jetty-config.xml.in

CLEANFILES = ${setup_SCRIPTS} \
    archiva-jetty-config.xml

