include libauto/project.am

volsdir = ${substdir}/vol

dist_vols_DATA = \
    vol/bodzdev

vol/bodzdev: Makefile
	cp -f /etc/coolcmd/subst/vol/bodzdev $@

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}
