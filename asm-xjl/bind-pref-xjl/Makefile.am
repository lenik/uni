include libauto/project.am

do_subst += -e 's,[@]bindconfdir[@],$(bindconfdir),g'

bindconfdir = ${sysconfdir}/bind

bindconf_DATA = \
    cooldns.options \
    lenik.key \
    alt.key

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    postinst \
    prerm
