include libauto/coolauto.am

SUBDIRS = lib aliases.d profile.d

shsrcdir   = ${bindir}

dist_bin_SCRIPTS = \
	eachdir \
	swapox \
	testox

dist_shsrc_DATA = \
	cd-ls \
	ls-which

dist_sysconf_DATA = \
    bash_aliases

dist_sudoers_DATA = \
	sudoers.d/keep-hist

setup_SCRIPTS = \
	postinst \
	prerm

EXTRA_DIST = \
	postinst.in \
	prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@

