include libauto/project.am

do_subst += \
    -e 's,[@]jettydir[@],$(jettydir),g' \
    -e 's,[@]jettyconfdir[@],$(jettyconfdir),g'

jettydir        = ${datadir}/jetty
jettyconfdir    = $(sysconfdir)/jetty

dist_jettyconf_DATA = \
    continuum-jetty-config.xml

dist_jettyconf_DATA = \
    continuum-override-web.xml

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    continuum-jetty-config.xml \
    ${dist_setup_SCRIPTS}
