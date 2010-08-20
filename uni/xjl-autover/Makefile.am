
SUBDIRS = lib

bin_SCRIPTS = \
	verinit \
	vercomp

dist_doc_DATA = \
	VERSION.sample \
	VERSION.template

man_MANS = \
	vercomp.5

CLEANFILES = verinit
EXTRA_DIST = verinit.in

do_subst = sed \
	-e 's,[@]datadir[@],$(datadir),g'

verinit: verinit.in Makefile
	$(do_subst) <$(srcdir)/verinit.in >verinit
