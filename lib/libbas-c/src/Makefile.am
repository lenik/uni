include ../libauto/coolauto.am

#AM_CFLAGS = -I../include

lib_LTLIBRARIES = libbas.la

libbas_la_LIBADD = ${GLIB_LIBS}
libbas_la_LDFLAGS = -version-info 1:0:0
libbas_la_CFLAGS = ${GLIB_CFLAGS} -I../include
dist_libbas_la_SOURCES = \
    cli.c \
    file.c
