include libauto/project.am

do_subst += \
    -e 's,[@]jettydir[@],$(jettydir),g' \
    -e 's,[@]jettylibdir[@],$(jettylibdir),g' \
    -e 's,[@]jettyetcdir[@],$(jettyetcdir),g' \
    -e 's,[@]jettyvardir[@],$(jettyvardir),g' \
    -e 's,[@]jettydatadir[@],$(jettydatadir),g'

jettydir = ${datadir}/jetty8
jettylibdir = ${jettydir}/lib
jettyetcdir = ${sysconfdir}/jetty8
jettyvardir = /var/lib/jetty8
jettydatadir = /var/lib/jetty8/derby

dist_jettyetc_DATA = \
    etc/env-appserver.xml \
    etc/env-derby.xml \
    etc/env-mail.xml \
    etc/env-users.xml

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}
