include libauto/coolauto.am

shsrcdir = ${bindir}

dist_bin_SCRIPTS = \
    tree-index

dist_shsrc_SCRIPTS = \
    cd-tree

dist_aliases_DATA = \
    bash_aliases.d/cd-tree

dist_bashcmpl_DATA = \
    bash_completion.d/cd-tree

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
