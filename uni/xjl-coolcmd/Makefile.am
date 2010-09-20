
include libauto/coolauto.am

dist_bin_SCRIPTS = \
	subst \
	substecho \
	substexec \
	substcat \
	rsubstcat \
	rsubstecho \
	rsexec \
	rsexec-c \
	ifrsubst \
	substtree \
	echox \
	swapox \
	testox \
	colorox

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
