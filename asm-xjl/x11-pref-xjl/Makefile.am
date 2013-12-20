include libauto/project.am

do_subst += \
    -e 's,[@]x11confdir[@],$(x11confdir),g' \
    -e 's,[@]xsessiondir[@],$(xsessiondir),g'

x11confdir  = ${sysconfdir}/X11
xsessiondir = ${x11confdir}/Xsession.d
bgdir       = ${datadir}/backgrounds

x11conf_DATA = \
    Xmodmap

dist_bg_DATA = \
    wallpapers/Boheme.jpg \
    wallpapers/Pointy.jpg

dist_xsession_DATA = \
    40load-xmodmap

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}
