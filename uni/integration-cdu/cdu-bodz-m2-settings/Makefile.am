include libauto/coolauto.am

mavenconfdir = ${sysconfdir}/maven2

do_subst += \
    -e 's,[@]mavenconfdir[@],$(mavenconfdir),g'

setup_DATA = \
    settings.xml

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
