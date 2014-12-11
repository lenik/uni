include ../libauto/common.am

includedir = ${prefix}/include
testdir = ${pkgdatadir}/test

AM_CFLAGS = ${GLIB_CFLAGS} -I../include
LDADD = -L../src -lbas

test_PROGRAMS = \
    line-no \
    t-log \
    t-str

dist_line_no_SOURCES = line-no.c
dist_t_log_SOURCES = t-log.c
dist_t_str_SOURCES = t-str.c
