
include libauto/coolauto.am

setup_SCRIPTS = \
    post-install.sh \
    pre-remove.sh

setup_DATA = \
	fstab.tc \
	passwd.tc

EXTRA_DIST = \
    fstab.tc \
    passwd.tc \
    post-install.sh.in \
    pre-remove.sh.in

CLEANFILES = \
    post-install.sh \
    pre-remove.sh

%: %.in Makefile
	$(do_subst) <$< >$@
