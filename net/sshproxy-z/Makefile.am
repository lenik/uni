include libauto/project.am

do_subst += \
    -e 's,[@]homedir[@],$(homedir),g' \
    -e 's,[@]sshconfdir[@],$(sshconfdir),g'

homedir    = /home/sshproxy
sshconfdir = ${homedir}/.ssh

dist_bin_SCRIPTS = \
    sudoscreen \
    sshproxy-z \
    srs

sshconf_DATA = \
    authorized_keys \
    id_rsa

init_DATA = \
    sshproxy-z.conf

dist_setup_SCRIPTS = \
    postinst-base.sh \
    postinst-server.sh \
    postinst-client.sh \
    prerm-base.sh \
    prerm-server.sh \
    prerm-client.sh

CLEANFILES = \
    ${dist_setup_SCRIPTS}
