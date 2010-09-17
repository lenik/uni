
include libauto/coolauto.am

sbin_SCRIPTS = \
	usecode

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
