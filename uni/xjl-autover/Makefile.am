
do_subst = sed \
    -e 's/master-file/generated-file, please do not edit/' \
    -e 's,[@]prefix[@],$(prefix),g' \
    -e 's,[@]bindir[@],$(bindir),g' \
    -e 's,[@]docdir[@],$(docdir),g' \
    -e 's,[@]datadir[@],$(datadir),g' \
    -e 's,[@]pkgdatadir[@],$(pkgdatadir),g'

SUBDIRS = lib

bin_SCRIPTS = \
    verinit \
    vercomp

dist_doc_DATA = \
    VERSION.sample

dist_pkgdata_DATA = \
    VERSION.template

man_MANS = \
    vercomp.5

CLEANFILES = verinit
EXTRA_DIST = verinit.in

verinit: verinit.in Makefile
	$(do_subst) <$(srcdir)/verinit.in >verinit
