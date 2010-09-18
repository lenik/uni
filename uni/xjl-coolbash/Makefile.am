include libauto/coolauto.am

SUBDIRS = aliases.d profile.d

shsrcdir   = ${bindir}
libbashdir = ${libdir}/bash

dist_bin_SCRIPTS = \
	eachdir \
	swapox \
	testox \
	echo-args \
	orig

dist_shsrc_DATA = \
	cd-ls \
	ls-which

dist_sysconf_DATA = \
    bash_aliases

dist_sudoers_DATA = \
	sudoers.d/keep-hist

libbash_DATA =

setup_SCRIPTS = \
	postinst \
	prerm

EXTRA_DIST = \
	postinst.in \
	prerm.in \
	lib

CLEANFILES = ${setup_SCRIPTS}

dist-hook:
	find ${distdir} -type d -name .svn -exec rm -fR {} \; -prune

install-data-hook:
	cp -RT ${srcdir}/lib ${DESTDIR}${libbashdir}
	find ${DESTDIR}${libbashdir} -type d -name .svn -exec rm -fR {} \; -prune

%: %.in Makefile
	$(do_subst) <$< >$@

