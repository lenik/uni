include libauto/coolauto.am

do_subst += \
    -e 's,[@]homedir[@],$(homedir),g' \
    -e 's,[@]sshconfdir[@],$(sshconfdir),g'

homedir    = /home/sshproxy
sshconfdir = ${homedir}/.ssh

sshconf_DATA = \
    authorized_keys \
    id_rsa

init_DATA = \
    sshproxy.conf

setup_SCRIPTS = \
    postinst-base.sh \
    postinst-server.sh \
    postinst-client.sh \
    prerm-base.sh \
    prerm-server.sh \
    prerm-client.sh

EXTRA_DIST = \
    sshproxy.conf.in \
    postinst-base.sh.in \
    postinst-server.sh.in \
    postinst-client.sh.in \
    prerm-base.sh.in \
    prerm-server.sh.in \
    prerm-client.sh.in

CLEANFILES = \
    sshproxy.conf \
    postinst-base.sh \
    postinst-server.sh \
    postinst-client.sh \
    prerm-base.sh \
    prerm-server.sh \
    prerm-client.sh

