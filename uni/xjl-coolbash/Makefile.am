include libauto/coolauto.am

SUBDIRS = aliases.d profile.d

shsrcdir = ${bindir}

bin_PROGRAMS = \
    echoln \
    echoli

dist_bin_SCRIPTS = \
    eachdir \
    orig \
    tree-index

dist_shsrc_SCRIPTS = \
    cd-ls \
    cd-first \
    cd..n \
    cd.x \
    cd-alias \
    cd-tree \
    d-which

dist_sudoers_DATA = \
    sudoers.d/keep-hist

dist_bashcmpl_DATA = \
    bash_completion.d/nosvn \
    bash_completion.d/cd-tree

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

