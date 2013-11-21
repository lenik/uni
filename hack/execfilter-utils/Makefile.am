include libauto/coolauto.am

SUBDIRS = test

bin_PROGRAMS = \
    yangwei

yangwei_SOURCES = yangwei.c ywcrypt.c

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    yangwei.c \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
