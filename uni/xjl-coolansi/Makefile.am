
include libauto/coolauto.am

dist_bin_SCRIPTS = \
    colors

bin_SCRIPTS = \
	ansifilter

man_MANS = \
	ansifilter.1

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
	postinst.in \
	prerm.in \
	get-ansifilter

CLEANFILES = \
	postinst \
	prerm \
	ansifilter \
	ansifilter.1

ansifilter ansifilter.1: get-ansifilter Makefile
	$(srcdir)/$< $@

%: %.in Makefile
	$(do_subst) <$< >$@
