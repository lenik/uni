include libauto/coolauto.am

shsrcdir = ${bindir}

dist_bin_SCRIPTS = \
    tree-index \
    xf-tree

dist_shsrc_SCRIPTS = \
    cd-tree

dist_aliases_DATA = \
    bash_aliases.d/cd-tree.sh

dist_completion_DATA = \
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
