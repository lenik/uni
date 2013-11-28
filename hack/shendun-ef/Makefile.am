include libauto/coolauto.am

cachedir = /var/cache
configdir = ${sysconfdir}/execfilter.d

do_subst += \
    -e 's,[@]cachedir[@],${cachedir},g' \
    -e 's,[@]configdir[@],${configdir},g'

SUBDIRS = test

bin_PROGRAMS = \
    yangwei

yangwei_SOURCES = yangwei.c ywcrypt.c
yangwei_CFLAGS = $(AM_CFLAGS)

dist_bin_SCRIPTS = \
    shendun-efpoll \
    yw-edit \
    .yw-edit.img

.yw-edit.img: yw-edit.i
	ai -ct alien $< >$@

lib_LTLIBRARIES = \
    libshendun-efext.la

libshendun_efext_la_SOURCES = shendun-efext.c ywcrypt.c
# libshendun_efext_la_LIBADD =

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