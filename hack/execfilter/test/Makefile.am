include ../libauto/common.am

configdir=${sysconfdir}/execfilter.d
demobindir = ${pkgdatadir}

#AM_CFLAGS = -g -Og
AM_CFLAGS = -g -O0

do_subst += \
    -e 's,[@]configdir[@],${configdir},g' \
    -e 's,[@]demobindir[@],${demobindir},g'

dist_config_DATA = \
    demo

demobin_PROGRAMS = \
    user

user_SOURCES = user.c
user_LDADD = -lbas

CLEANFILES = demo
