
include libauto/coolauto.am

msyncdir   = ${sysconfdir}/msync

msync_DATA =

setup_SCRIPTS = \
	preinst \
    postinst \
    prerm \
	postrm

EXTRA_DIST = \
	preinst.in \
	postinst.in \
	prerm.in \
	postrm.in \
	msync

CLEANFILES = ${setup_SCRIPTS}

dist-hook:
	find ${distdir}/msync -type d -name .svn -exec rm -fR {} \; -prune

install-data-hook:
	cp -RT ${srcdir}/msync ${DESTDIR}${msyncdir}
	find ${DESTDIR}${msyncdir} -type d -name .svn -exec rm -fR {} \; -prune

%: %.in Makefile
	$(do_subst) <$< >$@
