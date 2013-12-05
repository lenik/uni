include libauto/project.am
include libauto/allin.am

do_subst += -e 's,[@]profilesdir[@],$(profilesdir),g'

profilesdir = ${sysconfdir}/profile.d

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
