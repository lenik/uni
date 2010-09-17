
include libauto/coolauto.am


setupdir = ${datadir}/setup/${PACKAGE_NAME}

setup_SCRIPTS = \
	preinst \
    postinst \
    prerm \
	postrm

EXTRA_DIST = \
	preinst.in \
	postinst.in \
	prerm.in \
	postrm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
