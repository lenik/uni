include libauto/coolauto.am

SUBDIRS = libauto

bin_SCRIPTS = \
    autoinit \
	autoreconf2

dist_bin_SCRIPTS = \
    autounconf

dist_aliases_DATA = \
    aliases.d/autotools.sh

pkgdata_DATA = \
    automake.template

CLEANFILES = autoinit    autoreconf2    automake.template
EXTRA_DIST = autoinit.in autoreconf2.in automake.template.in

%: %.in Makefile
	$(do_subst) <$< >$@

