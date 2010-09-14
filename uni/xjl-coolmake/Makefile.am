
include libauto/coolauto.am

samplesdir = ${docdir}/samples
sysconfdir = /etc
aliasesdir = ${sysconfdir}/bash_aliases.d

dist_bin_SCRIPTS = \
	make-color \
    maketest \
    maketestvar \
    makelook \
	makeorig \
    subdirs

dist_samples_DATA = \
    samples/Makefile

dist_aliases_DATA = \
    coolmake.sh

EXTRA_DIST = makelook.in
CLEANFILES = makelook

makelook: makelook.in Makefile
	$(do_subst) <$(srcdir)/makelook.in >$@
