include _libauto/coolauto.am

SUBDIRS = _libauto

bin_SCRIPTS = \
    autoinit \
    autoreconf2

dist_bin_SCRIPTS = \
    autounconf

dist_aliases_DATA = \
    aliases.d/autotools.sh

pkgdata_DATA = \
    autoconf.template \
    automake.template

CLEANFILES = autoinit    autoreconf2    automake.template
EXTRA_DIST = autoinit.in autoreconf2.in automake.template.in

