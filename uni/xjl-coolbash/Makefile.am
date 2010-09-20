include libauto/coolauto.am

SUBDIRS = profile.d

shsrcdir   = ${bindir}

bin_PROGRAMS = \
	echoln \
	echoli

dist_bin_SCRIPTS = \
	eachdir \
	orig

dist_shsrc_DATA = \
	cd-ls \
	ls-which

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
	$(do_subst) \
		-e 's,[@]libbashdir[@],$(libbashdir),g' \
		<$< >$@

