do_subst = sed \
	-e 's/master-file/generated-file, please do not edit/' \
    -e 's,[@]prefix[@],$(prefix),g' \
    -e 's,[@]bindir[@],$(bindir),g' \
    -e 's,[@]docdir[@],$(docdir),g' \
    -e 's,[@]datadir[@],$(datadir),g' \
    -e 's,[@]pkgdatadir[@],$(pkgdatadir),g'

sysconfdir = /etc
aliasesdir = ${sysconfdir}/bash_aliases.d

dist_bin_SCRIPTS = \
    autoinit \
    autounconf

dist_aliases_DATA = \
    aliases.d/autotools.sh

dist_pkgdata_DATA = \
    autoconf.template \
    automake.template

CLEANFILES = autoinit
EXTRA_DIST = autoinit.in

autoinit: autoinit.in Makefile
	$(do_subst) <$(srcdir)/autoinit.in >autoinit

