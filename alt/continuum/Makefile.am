include libauto/project.am

do_subst += \
    -e 's,[@]jettydir[@],$(jettydir),g' \
    -e 's,[@]jettyconfdir[@],$(jettyconfdir),g'

jettydir        = ${datadir}/jetty
jettyconfdir    = $(sysconfdir)/jetty

jettyconf_DATA = \
    continuum-jetty-config.xml

dist_jettyconf_DATA = \
    continuum-override-web.xml

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in \
    continuum-jetty-config.xml.in

CLEANFILES = ${setup_SCRIPTS} \
    continuum-jetty-config.xml
