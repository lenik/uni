include libauto/project.am

do_subst += -e 's,[@]x11confdir[@],${x11confdir},g'

x11confdir = ${sysconfdir}/X11

dist_bin_SCRIPTS = \
    alt-exec \
    next-profile \
    noborder \
    open-url \
    run-sized \
    wsswitch

dist_x11conf_DATA = \
    x-alts

dist_man_MANS = \
    alt-exec.1 \
    noborder.1 \
    open-url.1 \
    run-sized.1 \
    wsswitch.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_setup_SCRIPTS}
