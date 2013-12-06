include ../libauto/common.am

includedir = ${prefix}/include

testdir = ${pkgdatadir}/test

test_PROGRAMS = \
    line-no \
    t-log

dist_line_no_SOURCES = \
    line-no.c
line_no_CFLAGS = ${GLIB_CFLAGS} -I../include
line_no_LDADD = -L../src -lbas

dist_t_log_SOURCES = \
    t-log.c
t_log_CFLAGS = -I../include
t_log_LDADD = -L../src -lbas
