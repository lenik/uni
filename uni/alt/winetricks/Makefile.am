include libauto/coolauto.am

dist_bin_SCRIPTS = \
    winetricks \
    winetricks-packages

winetricks:
	wget -O "$@" http://www.kegel.com/wine/winetricks
	chmod +x "$@"

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
