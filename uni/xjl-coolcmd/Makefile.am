
include libauto/coolauto.am

dist_bin_SCRIPTS = \
	subst \
	substexec \
	substcat \
	rsubstcat \
	rsubstecho \
	rsexec \
	rsexec-c \
	ifrsubst \
	substtree \
	echox

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
