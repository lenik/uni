include libauto/project.am

a2confdir = ${sysconfdir}/apache2/conf-available
awstatsconfdir = ${sysconfdir}/awstats

dist_a2conf_DATA = \
    awstats.conf

dist_awstatsconf_DATA = \
    awstats.example.conf

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}
