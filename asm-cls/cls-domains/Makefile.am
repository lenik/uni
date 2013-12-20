include libauto/project.am

SUBDIRS = lenik

do_subst += -e 's,[@]bindconfdir[@],$(bindconfdir),g'

bindconfdir = ${sysconfdir}/bind

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    postinst \
    prerm

