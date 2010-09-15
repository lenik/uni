
include libauto/coolauto.am

setupdir = ${datadir}/setup/${PACKAGE_NAME}

sysconfdir = /etc
initdir    = ${sysconfdir}/init
homedir    = /home/sshproxy
sshconfdir = ${homedir}/.ssh

init_DATA = \
	sshproxy.conf

dist_sshconf_DATA = \
	id_rsa

setup_SCRIPTS = \
    post-install.sh \
    pre-remove.sh

EXTRA_DIST = \
	post-install.sh.in \
	pre-remove.sh.in \
	sshproxy.conf.in

CLEANFILES = \
	post-install.sh \
	pre-remove.sh \
	sshproxy.conf

%: %.in Makefile
	$(do_subst) <$< >$@
