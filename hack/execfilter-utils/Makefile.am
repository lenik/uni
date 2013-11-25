include libauto/coolauto.am

cachedir = /var/cache
configdir = ${sysconfdir}/execfilter.d

do_subst += \
    -e 's,[@]cachedir[@],${cachedir},g' \
    -e 's,[@]configdir[@],${configdir},g'

SUBDIRS = test

bin_PROGRAMS = \
    yangwei

yangwei_SOURCES = yangwei.c
yangwei_LDADD = ywcrypt.o

dist_bin_SCRIPTS = \
    shendun-efpoll

lib_LTLIBRARIES = \
    libshendun-efext.la

libshendun_efext_la_SOURCES = shendun-efext.c ywcrypt.o
# libshendun_efext_la_LIBADD = ywcrypt.o

dist_config_DATA = \
    shendun.ini

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    yangwei.c \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
