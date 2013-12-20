include libauto/project.am

bindconfdir     = ${sysconfdir}/bind
dhcphookdir     = ${sysconfdir}/dhcp/dhclient-enter-hooks.d
dhcp3hookdir    = ${sysconfdir}/dhcp3/dhclient-enter-hooks.d

do_subst += \
    -e 's,[@]bindconfdir[@],$(bindconfdir),g' \
    -e 's,[@]dhcphookdir[@],$(dhcphookdir),g' \
    -e 's,[@]dhcp3hookdir[@],$(dhcp3hookdir),g'

dist_pkgdata_DATA = \
    ec2-forward.conf.in

dist_bindconf_DATA = \
    README.ec2-bind

dist_dhcphook_SCRIPTS = \
    ec2-forward-hook

dist_dhcp3hook_SCRIPTS = \
    ec2-forward-hook

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}
