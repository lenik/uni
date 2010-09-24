include libauto/coolauto.am

dist_subst_DATA = \
    vol/bodzdev

vol/bodzdev: Makefile
	sudo ec2-update-subst
	cp /etc/coolcmd/subst/vol/bodzdev $@

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
