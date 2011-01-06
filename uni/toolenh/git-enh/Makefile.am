include libauto/coolauto.am

dist_bin_SCRIPTS = \
    keepdirs

dist_aliases_DATA = \
    git-enh.alt

aliases_DATA = \
    sw-git.sh

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in \
    sw-git.sh.in

CLEANFILES = ${setup_SCRIPTS} \
    sw-git.sh

%: %.in Makefile
	$(do_subst) <$< >$@
