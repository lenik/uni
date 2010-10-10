include libauto/coolauto.am

SUBDIRS = profile.d

bin_PROGRAMS = \
    echoln \
    echoli

dist_bin_SCRIPTS = \
    eachdir \
    orig

dist_sudoers_DATA = \
    sudoers.d/keep-hist

dist_bashcmpl_DATA = \
    bash_completion.d/nosvn

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

