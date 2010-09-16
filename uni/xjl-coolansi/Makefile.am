
include libauto/coolauto.am

dist_bin_SCRIPTS = \
    colors

bin_SCRIPTS = \
	ansifilter

man_MANS = \
	ansifilter.1

setup_SCRIPTS = \
    post-install.sh \
    pre-remove.sh

EXTRA_DIST = \
	post-install.sh.in \
	pre-remove.sh.in \
	get-ansifilter

CLEANFILES = \
	post-install.sh \
	pre-remove.sh \
	ansifilter \
	ansifilter.1

ansifilter ansifilter.1: get-ansifilter Makefile
	$(srcdir)/$< $@

%: %.in Makefile
	$(do_subst) <$< >$@
