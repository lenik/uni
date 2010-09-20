include libauto/coolauto.am

bin_SCRIPTS = \
	shlib

dist_bin_SCRIPTS = \
	shlib-import

shlib_DATA =

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
	postinst.in \
	prerm.in \
	shlib.in \
	shlib.d

CLEANFILES = \
	${setup_SCRIPTS} \
	shlib

%: %.in Makefile
	$(do_subst) <$< >$@

dist-hook:
	find ${distdir}/shlib.d -type d -name .svn -exec rm -fR {} \; -prune

install-data-hook:
	cp -RT ${srcdir}/shlib.d ${DESTDIR}${shlibdir}
	find ${DESTDIR}${shlibdir} -type d -name .svn -exec rm -fR {} \; -prune
