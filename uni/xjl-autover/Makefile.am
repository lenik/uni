include libauto/coolauto.am

SUBDIRS = lib

AM_CPPFLAGS = ${GLIB_CFLAGS}
vercomp_LDADD = $(GLIB_LIBS)

bin_PROGRAMS = \
    vercomp

bin_SCRIPTS = \
    verinit

dist_bin_SCRIPTS = \
    autover \
    ver2int

dist_man_MANS = \
    autover.1 \
    verinit.1 \
    vercomp.1 \
    vercomp.5 \
    ver2int.1

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
