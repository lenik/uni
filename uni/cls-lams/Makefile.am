include libauto/coolauto.am

setup_SCRIPTS = \
    postinst \
    prerm

setup_DATA = \
    fstab.tc \
    passwd.tc

EXTRA_DIST = \
    fstab.tc \
    passwd.tc \
    postinst.in \
    prerm.in

CLEANFILES = \
    postinst \
    prerm

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"
