include libauto/coolauto.am

dist_bin_SCRIPTS= \
    img2vdi \
    vdi2img \
    loop2vmdk \
    raw-vmdk

dist_man_MANS = \
    img2vdi.1 \
    vdi2img.1 \
    loop2vmdk.1 \
    raw-vmdk.1

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
