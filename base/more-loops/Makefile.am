include libauto/project.am

modconfdir = ${sysconfdir}/modprobe.d

dist_modconf_DATA = \
    loop.conf

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
