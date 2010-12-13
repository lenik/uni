include libauto/coolauto.am

volsdir = ${substdir}/vol

dist_vols_DATA = \
    vol/bodzdev

vol/bodzdev: /etc/coolcmd/subst/vol/bodzdev
	cp -f $< $@

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

