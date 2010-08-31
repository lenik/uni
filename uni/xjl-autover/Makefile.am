
include libauto/subst.am

SUBDIRS = lib

AM_CPPFLAGS = ${GLIB_CFLAGS}
vercomp_LDADD = $(GLIB_LIBS)

CFLAGS = -g -O0

bin_PROGRAMS = \
	vercomp

dist_bin_SCRIPTS = \
    verinit

dist_doc_DATA = \
    VERSION.sample

dist_pkgdata_DATA = \
    VERSION.template

dist_man_MANS = \
    vercomp.5

CLEANFILES = verinit
EXTRA_DIST = verinit.in

verinit: verinit.in Makefile
	$(do_subst) <$(srcdir)/verinit.in >verinit
