include libauto/coolauto.am

qlogconfdir = ${pkgdatadir}

dist_bin_SCRIPTS = \
    cooltask_list \
    qlog

dist_qlogconf_DATA = \
    qlog.conf/focus \
    qlog.conf/priority

dist_aliases_DATA = \
    cooltask.sh

dist_bashcmpl_DATA = \
    cooltask

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS} \
    cooltask_list \
    qlog

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"
