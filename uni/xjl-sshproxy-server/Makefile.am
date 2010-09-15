
include libauto/coolauto.am

setupdir = ${datadir}/setup/${PACKAGE_NAME}

pkgdatadir = \
	sshproxy.pub

setup_SCRIPTS = \
    post-install.sh \
    pre-remove.sh

EXTRA_DIST = \
	post-install.sh.in \
	pre-remove.sh.in \
	coolsocks.conf.in

CLEANFILES = \
	post-install.sh \
	pre-remove.sh \
	coolsocks.conf

%: %.in Makefile
	$(do_subst) \
		-e 's,[@]<$< >$@
