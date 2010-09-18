include libauto/coolauto.am

libbashdir = ${libdir}/bash

libbash_DATA =

dist-hook:
	find ${distdir} -type d -name .svn -exec rm -fR {} \; -prune

install-data-hook:
	cp -RT ${srcdir}/lib ${DESTDIR}${libbashdir}
	find ${DESTDIR}${libbashdir} -type d -name .svn -exec rm -fR {} \; -prune

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
	postinst.in \
	prerm.in \
	lib

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
