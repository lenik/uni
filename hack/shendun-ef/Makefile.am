include libauto/coolauto.am

AI_TEMPLATE=alien

cachedir = /var/cache
configdir = ${sysconfdir}/execfilter.d
cronhourlydir = ${sysconfdir}/cron.hourly

do_subst += \
    -e 's,[@]cachedir[@],${cachedir},g' \
    -e 's,[@]configdir[@],${configdir},g'

SUBDIRS = test

bin_PROGRAMS = \
    yangwei

yangwei_SOURCES = yangwei.c ywcrypt.c
yangwei_CFLAGS = $(GLIB_CFLAGS) $(AM_CFLAGS)
yangwei_LDADD = -lbas

dist_bin_SCRIPTS = \
    shendun-efpoll \
    yw-edit

dist_pkgdata_DATA = \
    shendun-efpoll.img \
    yw-edit.img

lib_LTLIBRARIES = \
    libshendun-efext.la

libshendun_efext_la_SOURCES = shendun-efext.c ywcrypt.c
# libshendun_efext_la_LIBADD =

dist_config_DATA = \
    shendun.ini

dist_cronhourly_SCRIPTS = \
    shendun-renew

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    yangwei.c \
    postinst.in \
    prerm.in

CLEANFILES = \
    ${setup_SCRIPTS} \
    ${dist_bin_SCRIPTS}
