
include libauto/project.am

msyncdir   = ${sysconfdir}/msync

msync_DATA =

EXTRA_DIST = \
    msync

CLEANFILES =

dist-hook:
	find ${distdir}/msync -type d -name .svn -exec rm -fR {} \; -prune

install-data-hook:
	cp -RT ${srcdir}/msync ${DESTDIR}${msyncdir}
	find ${DESTDIR}${msyncdir} -type d -name .svn -exec rm -fR {} \; -prune
