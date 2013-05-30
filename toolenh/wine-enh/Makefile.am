include libauto/coolauto.am

SUBDIRS = binfmts

dist_bin_SCRIPTS = \
    exe-runner \
    install-exe

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${setup_SCRIPTS}
