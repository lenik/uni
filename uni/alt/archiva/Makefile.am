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
    archiva-jetty-config.xml \
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

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"

install-data-hook:
	mkdir -p $(DESTDIR)$(jettylibdir); \
        ln -vsnf $(javalibdir)/derby.jar $(DESTDIR)$(jettylibdir)
