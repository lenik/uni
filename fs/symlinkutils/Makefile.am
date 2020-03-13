include libauto/project.am

dist_bin_SCRIPTS = \
    symltrace

dist_man_MANS = \
    symltrace.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"
