include ../libauto/coolauto.am

configdir=${sysconfdir}/execfilter.d
demobindir = ${pkgdatadir}

do_subst += \
    -e 's,[@]configdir[@],${configdir},g' \
    -e 's,[@]demobindir[@],${demobindir},g'

dist_config_DATA = \
    demo

demobin_PROGRAMS = \
    user

bin_PROGRAMS = \
    qstr-tst

user_SOURCES = user.c
user_CFLAGS = -g -Og

qstr_tst_SOURCES = qstr-tst.c
qstr_tst_LDADD = ../qstr.o

CLEANFILES = demo
