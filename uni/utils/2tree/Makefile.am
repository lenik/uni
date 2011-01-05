include libauto/coolauto.am

SUBDIRS = test

dist_bin_SCRIPTS = \
    atree \
    2tree

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
