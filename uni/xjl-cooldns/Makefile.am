
include libauto/coolauto.am

setupdir = ${datadir}/setup/${PACKAGE_NAME}

sysconfdir = /etc
bindconfdir = ${sysconfdir}/bind

bindconf_DATA = \
	bind/cooldns.options

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
	$(do_subst) \
		-e 's,[@]bindconfdir[@],$(bindconfdir),g' \
		<$< >$@
