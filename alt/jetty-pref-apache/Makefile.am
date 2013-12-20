include libauto/project.am

do_subst += \
    -e 's,[@]jettydir[@],$(jettydir),g' \
    -e 's,[@]jettylibdir[@],$(jettylibdir),g' \
    -e 's,[@]jettyconfdir[@],$(jettyconfdir),g' \
    -e 's,[@]databasedir[@],$(databasedir),g' \
    -e 's,[@]userdir[@],$(userdir),g'

jettydir        = ${datadir}/jetty
jettylibdir     = $(jettydir)/lib
jettyconfdir    = $(sysconfdir)/jetty
userdir         = /var/lib/jetty
databasedir     = $(userdir)/derby

dist_jettyconf_DATA = \
    env-appserver.xml \
    env-derby.xml \
    env-mail.xml \
    env-shutdown.xml \
    env-users.xml

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    env-derby.xml \
    ${dist_setup_SCRIPTS}
