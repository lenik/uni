include libauto/project.am

mavenconfdir = ${sysconfdir}/maven
maven2confdir= ${sysconfdir}/maven2

do_subst += \
    -e 's,[@]mavenconfdir[@],$(mavenconfdir),g' \
    -e 's,[@]maven2confdir[@],$(maven2confdir),g'

setup_DATA = \
    settings.xml

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}
