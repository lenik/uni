include libauto/project.am

setup_DATA = \
    svn2cl.diff \
    svn2cl.xsl.diff

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

svn2cl.diff: svn2cl.0 svn2cl
	-diff -u ${srcdir}/svn2cl.0 ${srcdir}/svn2cl >$@

svn2cl.xsl.diff: svn2cl.xsl.0 svn2cl.xsl
	-diff -u ${srcdir}/svn2cl.xsl.0 ${srcdir}/svn2cl.xsl >$@

%: %.in Makefile
	$(do_subst) <$< >$@
