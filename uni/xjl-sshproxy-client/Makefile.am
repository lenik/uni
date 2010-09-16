
include libauto/coolauto.am

setupdir = ${datadir}/setup/${PACKAGE_NAME}

sysconfdir = /etc
initdir    = ${sysconfdir}/init
homedir    = /home/sshproxy
sshconfdir = ${homedir}/.ssh

setup_SCRIPTS = \
    post-install.sh \
    pre-remove.sh

setup_DATA = \
	id_rsa

EXTRA_DIST = \
	post-install.sh.in \
	pre-remove.sh.in \
	sshproxy.conf.in

CLEANFILES = \
	post-install.sh \
	pre-remove.sh \
	sshproxy.conf

%: %.in Makefile
	$(do_subst) \
		-e 's,[@]homedir[@],$(homedir),g' \
		-e 's,[@]sshconfdir[@],$(sshconfdir),g' \
		<$< >$@
