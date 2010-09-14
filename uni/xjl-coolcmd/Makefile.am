
include libauto/coolauto.am

dist_bin_SCRIPTS = \
	subst \
	substexec \
	substcat \
	rsubstcat \
	rsubstecho \
	rsexec \
	rsexec-c \
	ifrsubst \
	substtree

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
