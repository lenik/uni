include libauto/coolauto.am

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

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in \
    env-derby.xml.in

CLEANFILES = ${setup_SCRIPTS} \
    env-derby.xml

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"
