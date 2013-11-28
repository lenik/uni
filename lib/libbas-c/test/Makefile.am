include ../libauto/coolauto.am

includedir = ${prefix}/include

testdir = ${pkgdatadir}/test

test_PROGRAMS = \
    line-no

dist_line_no_SOURCES = \
    line-no.c
line_no_CFLAGS = ${GLIB_CFLAGS} -I../include
line_no_LDADD = -L../src -lbas
