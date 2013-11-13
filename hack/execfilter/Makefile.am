include libauto/coolauto.am

ACLOCAL_AMFLAGS = -I m4
AM_CPPFLAGS = $(GLIB_CFLAGS)

pkglib_LTLIBRARIES = \
    libtrie.la

libtrie_la_SOURCES = trie.c
libtrie_la_LIBADD = $(GLIB_LIBS)

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
