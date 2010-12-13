include libauto/coolauto.am
include libauto/allin.am

dist_bin_SCRIPTS = \
    winetricks \
    winetricks-packages

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

winetricks:
	wget -O "$@" http://www.kegel.com/wine/winetricks
	chmod +x "$@"

