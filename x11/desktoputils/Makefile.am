include libauto/project.am

do_subst += -e 's,[@]x11confdir[@],${x11confdir},g'

x11confdir = ${sysconfdir}/X11

dist_bin_SCRIPTS = \
    alt-exec \
    getpidwin \
    movewindow \
    next-profile \
    noborder \
    open-url \
    run-sized \
    wsswitch

dist_shlib_DATA = \
    shlib/winfn

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

install-debug:
	for a in ${dist_bin_SCRIPTS}; do \
            sudo ln -snf $$PWD/$$a $(DESTDIR)$(bindir); \
        done
	sudo ln -snf $$PWD/shlib/* $(DESTDIR)$(shlibdir)

