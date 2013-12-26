include libauto/private.am
include libauto/compat.am

AI_TEMPLATE=alien

configdir = ${sysconfdir}/execfilter.d
cronhourlydir = ${sysconfdir}/cron.hourly
specbindir = ${pkgdatadir}
rundir = /var/run

do_subst += \
    -e 's,[@]configdir[@],${configdir},g' \
    -e 's,[@]cronhourlydir[@],${cronhourlydir},g' \
    -e 's,[@]specbindir[@],${specbindir},g' \
    -e 's,[@]rundir[@],${rundir},g'

SUBDIRS = test

specbin_PROGRAMS = \
    yangwei

yangwei_SOURCES = yangwei.c ywcrypt.c
yangwei_CFLAGS = $(GLIB_CFLAGS) $(AM_CFLAGS)
yangwei_LDADD = -lbas

#efgate_SOURCES = shendun-efext.c ywcrypt.c
#efgate_CFLAGS = ${AM_CFLAGS}
#efgate_LDADD = -lbas

dist_bin_SCRIPTS = \
    shendun-efpoll \
    yw-edit

dist_pkgdata_DATA = \
    shendun-efpoll.img \
    yw-edit.img

lib1_LTLIBRARIES = \
    libshendun-efext.la

libshendun_efext_la_SOURCES = \
    shendun-efext.c \
    ywcrypt.c
libshendun_efext_la_LDFLAGS = -version-info 1:0:0
libshendun_efext_la_LIBADD = -lbas

dist_config_DATA = \
    shendun.ini

dist_cronhourly_SCRIPTS = \
    shendun-renew

dist_man_MANS = \
    shendun-efpoll.8 \
    yangwei.1 \
    yw-edit.8

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS} \
    ${dist_bin_SCRIPTS}
