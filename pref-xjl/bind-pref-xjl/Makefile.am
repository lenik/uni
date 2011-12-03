include libauto/coolauto.am

do_subst += -e 's,[@]bindconfdir[@],$(bindconfdir),g'

bindconfdir = ${sysconfdir}/bind

bindconf_DATA = \
    cooldns.options \
    lenik.key \
    alt.key

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    postinst \
    prerm

