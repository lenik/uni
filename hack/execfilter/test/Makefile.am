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

user_SOURCES = user.c

CLEANFILES = demo
