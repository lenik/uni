include libauto/project.am

mavenconfdir = ${sysconfdir}/maven
maven2confdir= ${sysconfdir}/maven2

do_subst += \
    -e 's,[@]mavenconfdir[@],$(mavenconfdir),g' \
    -e 's,[@]maven2confdir[@],$(maven2confdir),g'

setup_DATA = \
    settings.xml

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
