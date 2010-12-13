include libauto/coolauto.am

SUBDIRS = lenik

do_subst += -e 's,[@]bindconfdir[@],$(bindconfdir),g'

bindconfdir = ${sysconfdir}/bind

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    postinst \
    prerm

