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

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS} \
    a2proxy

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"
