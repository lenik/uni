include libauto/project.am
include libauto/allin.am

do_subst += -e 's,[@]profilesdir[@],$(profilesdir),g'

profilesdir = ${sysconfdir}/profile.d

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}
