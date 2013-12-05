include libauto/project.am

qlogconfdir = ${pkgdatadir}

dist_bin_SCRIPTS = \
    cooltask_list \
    mtry \
    qlog \
    qlog-console

dist_qlogconf_DATA = \
    qlog.conf/focus \
    qlog.conf/priority

dist_aliases_DATA = \
    cooltask.sh

dist_completion_DATA = \
    cooltask

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS} \
    ${dist_bin_SCRIPTS} \
    cooltask_list \
    qlog \
    qlog-console

#%: %.in Makefile
#	$(do_subst) <$< >$@
#	chmod -w "$@"
