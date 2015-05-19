include libauto/project.am

SUBDIRS = db

do_subst += -e 's,[@]bindconfdir[@],$(bindconfdir),g'

bindconfdir = ${sysconfdir}/bind

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    postinst \
    prerm

