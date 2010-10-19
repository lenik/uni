include libauto/coolauto.am

dist_sbin_SCRIPTS = \
    dwhich

dist_man_MANS = \
    dwhich.1

dist_aliases_DATA = \
    aptutils.sh

dist_bashcmpl_DATA = \
    aptutils_compl.sh

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
