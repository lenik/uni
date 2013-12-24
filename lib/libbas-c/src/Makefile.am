include ../libauto/common.am

lib2dir = /usr/lib

#AM_CFLAGS = -I../include

lib2_LTLIBRARIES = libbas.la

libbas_la_LIBADD = ${GLIB_LIBS}
libbas_la_LDFLAGS = -version-info 1:0:0
libbas_la_CFLAGS = -I../include ${GLIB_CFLAGS}
dist_libbas_la_SOURCES = \
    cli.c \
    file.c \
    log.c \
    path.c \
    proc.c \
    str.c \
    syslog.c
