include libauto/coolauto.am

samplesdir = ${docdir}/samples

dist_bin_SCRIPTS = \
    make-color \
    maketest \
    maketestvar \
    makelook \
    makeorig \
    subdirs \
    autobuild

dist_samples_DATA = \
    samples/Makefile

dist_aliases_DATA = \
    coolmake.sh

EXTRA_DIST = makelook.in
CLEANFILES = makelook

makelook: makelook.in Makefile
	$(do_subst) <$(srcdir)/makelook.in >$@
