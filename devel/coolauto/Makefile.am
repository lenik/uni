include _libauto/project.am

SUBDIRS = _libauto

dist_bin_SCRIPTS = \
    autoinit \
    autoreconf2 \
    autounconf

dist_man_MANS = \
    autoinit.1 \
    autoreconf2.1 \
    autounconf.1

dist_aliases_DATA = \
    aliases.d/autotools.sh

dist_pkgdata_DATA = \
    autoconf.template \
    automake.template

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_man_MANS}

