
include libauto/coolauto.am

sbin_SCRIPTS = \
	usecode

setup_SCRIPTS = \
    post-install.sh \
    pre-remove.sh

EXTRA_DIST = \
	post-install.sh.in \
	pre-remove.sh.in

CLEANFILES = \
	post-install.sh \
	pre-remove.sh

%: %.in Makefile
	$(do_subst) <$< >$@
