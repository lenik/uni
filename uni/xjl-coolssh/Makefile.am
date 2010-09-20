include libauto/coolauto.am

dist_sbin_SCRIPTS = \
	ssh-acceptkeys \
	ssh-globalknownhosts \
	ssh-shortcuts

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
