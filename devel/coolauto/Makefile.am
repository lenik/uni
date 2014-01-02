include _libauto/project.am

libautodir = $(pkgdatadir)/libauto

dist_bin_SCRIPTS = \
    autoinit \
    autoreconf2 \
    autounconf

dist_man_MANS = \
    autoinit.1 \
    autoreconf2.1 \
    autounconf.1

dist_alias_DATA = \
    aliases.d/autotools.sh

dist_pkgdata_DATA = \
    autoconf.template \
    automake.template

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_man_MANS}

install-data-hook:
	cp -R $(srcdir)/_libauto $(DESTDIR)$(libautodir)
