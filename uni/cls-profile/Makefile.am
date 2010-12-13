
include libauto/coolauto.am


setupdir = ${datadir}/setup/${PACKAGE_NAME}

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    postinst \
    prerm

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"
