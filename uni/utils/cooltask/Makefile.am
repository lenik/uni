include libauto/coolauto.am

dist_bin_SCRIPTS = \
    cooltask_list

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

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"
