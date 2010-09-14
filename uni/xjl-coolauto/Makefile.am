include libauto/coolauto.am

SUBDIRS = libauto

sysconfdir = /etc
aliasesdir = ${sysconfdir}/bash_aliases.d

bin_SCRIPTS = \
    autoinit \
	autoreconf2
dist_bin_SCRIPTS = \
    autounconf

dist_aliases_DATA = \
    aliases.d/autotools.sh

dist_pkgdata_DATA = \
    autoconf.template
pkgdata_DATA = \
    automake.template

CLEANFILES = autoinit autoreconf2 automake.template
EXTRA_DIST = autoinit.in autoreconf2.in automake.template.in

autoinit: autoinit.in Makefile
	$(do_subst) <$(srcdir)/autoinit.in >$@

autoreconf2: autoreconf2.in Makefile
	$(do_subst) <$(srcdir)/autoreconf2.in >$@

automake.template: automake.template.in Makefile
	$(do_subst) <$(srcdir)/automake.template.in >$@

