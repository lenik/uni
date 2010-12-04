include libauto/coolauto.am

dist_bin_SCRIPTS= \
    img2vdi \
    vdi2img

dist_man_MANS = \
    img2vdi.1 \
    vdi2img.1

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
