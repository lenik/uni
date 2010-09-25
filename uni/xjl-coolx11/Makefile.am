include libauto/coolauto.am

x11confdir  = ${sysconfdir}/X11
xsessiondir = ${x11confdir}/Xsession.d

x11conf_DATA = \
    Xmodmap

dist_xsession_DATA = \
    load-xmodmap

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in \
    load-xmodmap.in

CLEANFILES = ${setup_SCRIPTS} \
    load-xmodmap

%: %.in Makefile
	$(do_subst) \
        -e 's,[@]x11confdir[@],$(x11confdir),g' \
        -e 's,[@]xsessiondir[@],$(xsessiondir),g' \
        <$< >$@
