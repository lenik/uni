include libauto/project.am

do_subst += -e 's,[@]x11confdir[@],${x11confdir},g'

x11confdir = ${sysconfdir}/X11

dist_bin_SCRIPTS = \
    alt-exec \
    open-url \
    wsswitch

dist_x11conf_DATA = \
    x-alts

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}
