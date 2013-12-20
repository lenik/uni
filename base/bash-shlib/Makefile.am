include libauto/project.am

SUBDIRS = shlib.d

dist_bin_SCRIPTS = \
    findabc1 \
    shlib \
    shlib-import

dist_man_MANS = \
    findabc1.1 \
    shlib.1 \
    shlib-import.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    shlib.d

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}

