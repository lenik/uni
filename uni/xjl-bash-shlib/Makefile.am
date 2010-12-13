include libauto/coolauto.am

SUBDIRS = shlib.d

bin_SCRIPTS = \
    shlib

dist_bin_SCRIPTS = \
    shlib-import

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in \
    shlib.in \
    shlib.d

CLEANFILES = \
    ${setup_SCRIPTS} \
    shlib

