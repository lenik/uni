include libauto/coolauto.am

SUBDIRS = aliases.d

dist_sysconf_DATA = \
    bash_aliases

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
	postinst.in \
	prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
