include libauto/private.am
include libauto/compat.am

AI_TEMPLATE=alien

configdir = ${sysconfdir}/execfilter.d
crondir = ${sysconfdir}/cron.d
cronhourlydir = ${sysconfdir}/cron.hourly
specbindir = ${pkgdatadir}
rundir = /var/run

do_subst += \
    -e 's,[@]configdir[@],${configdir},g' \
    -e 's,[@]crondir[@],${crondir},g' \
    -e 's,[@]cronhourlydir[@],${cronhourlydir},g' \
    -e 's,[@]specbindir[@],${specbindir},g' \
    -e 's,[@]rundir[@],${rundir},g'

SUBDIRS = test

specbin_PROGRAMS = \
    yangwei

yangwei_SOURCES = yangwei.c ywcrypt.c ywcrypt.h
yangwei_CFLAGS = $(GLIB_CFLAGS) $(AM_CFLAGS)
yangwei_LDADD = -lbas

#efgate_SOURCES = sdefext.c ywcrypt.c
#efgate_CFLAGS = ${AM_CFLAGS}
#efgate_LDADD = -lbas

dist_bin_SCRIPTS = \
    sdefpoll \
    yw-edit

dist_pkgdata_DATA = \
    sdefpoll.img \
    yw-edit.img

dist_pkgdata_SCRIPTS = \
    sdefinit

rootlib_LTLIBRARIES = \
    libsdefext.la

libsdefext_la_SOURCES = \
    sdefext.c \
    ywcrypt.c \
    ywcrypt.h \
    log.h
libsdefext_la_LDFLAGS = -version-info 1:0:0
libsdefext_la_LIBADD = -lbas

dist_config_DATA = \
    shendun.ini

dist_cron_DATA = \
    sdefchk

dist_cronhourly_SCRIPTS = \
    sdefre

dist_man_MANS = \
    sdefpoll.8 \
    yangwei.1 \
    yw-edit.8

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS} \
    ${dist_bin_SCRIPTS}

installer:
	tar czf osp.tgz -C osp/ .
	debinst -u osp.tgz --deb-word=ubnt --rpm-word=rhel shendun-ef
	debinst -u osp.tgz --deb-word=ubnt --rpm-word=rhel shendun-ef-dbg
	mkdir -p /tmp/html; mv *.run /tmp/html
