include libauto/project.am

sitesdir = ${sysconfdir}/apache2/sites-available

dist_sysconf_DATA = \
    mini-dinstall.conf

dist_sites_DATA = \
    dlocal.conf

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
