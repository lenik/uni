include libauto/coolauto.am

configdir=${sysconfdir}/execfilter.d

do_subst += -e 's,[@]configdir[@],${configdir},g'

ACLOCAL_AMFLAGS = -I m4
AM_CPPFLAGS = $(GLIB_CFLAGS)

SUBDIRS = test

lib_LTLIBRARIES = \
    libexecfilter.la \
    libefstub.la

libexecfilter_la_SOURCES = \
    filter.c \
    path.c \
    qstr.c \
    trie.c \
    posix/exec.c \
    posix/popen.c \
    posix/spawn.c \
    posix/system.c

libexecfilter_la_LIBADD = $(GLIB_LIBS) -ldl
libexecfilter_la_LDFLAGS = -version-info 1:0:0

libefstub_la_SOURCES = stub.c
libefstub_la_LDFLAGS = -version-info 1:0:0

dist_doc_DATA = manual.pdf

manual.pdf: main.tex
	cooltex -a -jmanual main.tex

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    ${setup_SCRIPTS}
