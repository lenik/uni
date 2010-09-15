
include libauto/coolauto.am

setupdir   = ${datadir}/setup/${PACKAGE_NAME}
homedir    = /home/sshproxy
sshconfdir = ${homedir}/.ssh

setup_SCRIPTS = \
    post-install.sh \
    pre-remove.sh

setup_DATA = \
	authorized_keys

EXTRA_DIST = \
	post-install.sh.in \
	pre-remove.sh.in

CLEANFILES = \
	post-install.sh \
	pre-remove.sh

%: %.in Makefile
	$(do_subst) \
		-e 's,[@]homedir[@],$(homedir),g' \
		-e 's,[@]sshconfdir[@],$(sshconfdir),g' \
		<$< >$@
