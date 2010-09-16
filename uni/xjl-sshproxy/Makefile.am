
include libauto/coolauto.am

setupdir   = ${datadir}/setup/${PACKAGE_NAME}
sysconfdir = /etc
initdir    = ${sysconfdir}/init
homedir    = /home/sshproxy
sshconfdir = ${homedir}/.ssh

sshconf_DATA = \
	authorized_keys \
	id_rsa

setup_SCRIPTS = \
	postinst-base.sh \
	postinst-server.sh \
	postinst-client.sh \
	prerm-base.sh \
	prerm-server.sh \
	prerm-client.sh

EXTRA_DIST = \
	postinst-base.sh.in \
	postinst-server.sh.in \
	postinst-client.sh.in \
	prerm-base.sh.in \
	prerm-server.sh.in \
	prerm-client.sh.in

CLEANFILES = \
	postinst-base.sh \
	postinst-server.sh \
	postinst-client.sh \
	prerm-base.sh \
	prerm-server.sh \
	prerm-client.sh

%: %.in Makefile
	$(do_subst) \
		-e 's,[@]homedir[@],$(homedir),g' \
		-e 's,[@]sshconfdir[@],$(sshconfdir),g' \
		<$< >$@
