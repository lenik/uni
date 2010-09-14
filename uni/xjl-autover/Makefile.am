
include libauto/coolauto.am

SUBDIRS = lib

sysconfdir = /etc
aliasesdir = ${sysconfdir}/bash_aliases.d

AM_CPPFLAGS = ${GLIB_CFLAGS}
vercomp_LDADD = $(GLIB_LIBS)

bin_PROGRAMS = \
	vercomp

bin_SCRIPTS = \
    verinit

dist_bin_SCRIPTS = \
	autover

dist_man_MANS = \
	autover.1 \
	verinit.1 \
	vercomp.1 \
    vercomp.5

dist_doc_DATA = \
    VERSION.sample

dist_pkgdata_DATA = \
    VERSION.template

dist_aliases_DATA = \
	aliases.d/autover.sh

EXTRA_DIST = verinit.in
CLEANFILES = verinit

%: %.in Makefile
	$(do_subst) <$< >$@
