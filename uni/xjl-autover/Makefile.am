
include libauto/subst.am

SUBDIRS = lib

sysconfdir = /etc
aliasesdir = ${sysconfdir}/bash_aliases.d

AM_CPPFLAGS = ${GLIB_CFLAGS}
vercomp_LDADD = $(GLIB_LIBS)

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

dist_aliases_DATA = \
	aliases.d/ver-aliases.sh

CLEANFILES = verinit
EXTRA_DIST = verinit.in

verinit: verinit.in Makefile
	$(do_subst) <$(srcdir)/verinit.in >verinit
