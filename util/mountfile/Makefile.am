include libauto/project.am

dist_bin_SCRIPTS = \
    mountfile

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
