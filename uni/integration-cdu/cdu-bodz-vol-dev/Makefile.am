include libauto/coolauto.am

volsdir = ${substdir}/vol

dist_vols_DATA = \
    vol/bodzdev

vol/bodzdev: Makefile
	cp -f /etc/coolcmd/subst/vol/bodzdev $@

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

