include libauto/project.am

sitesdir = ${sysconfdir}/apache2/sites-available
do_subst += -e 's,[@]sitesdir[@],${sitesdir},g'

dist_bin_SCRIPTS = \
    a2logtail \
    a2reload \
    a2restart \
    a2start \
    a2stop \
    a2proxy

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS} \
    a2proxy
