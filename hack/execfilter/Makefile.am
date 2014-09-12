include libauto/private.am
include libauto/compat.am
include libauto/env/tex.am

configdir = ${sysconfdir}/execfilter.d

do_subst += \
    -e 's,[@]configdir[@],${configdir},g'

ACLOCAL_AMFLAGS = -I m4
AM_CPPFLAGS = $(GLIB_CFLAGS) -DLOCALEDIR=\"$(localedir)\"
#AM_CFLAGS = -Wall -g -O0

SUBDIRS = po test

bin_PROGRAMS = \
    ef-dump

ef_dump_SOURCES = ef-dump.c ${libexecfilter_la_SOURCES}
# ef_dump_LDADD = -lexecfilter
ef_dump_CFLAGS = ${AM_CFLAGS}
ef_dump_LDADD = ${GLIB_LIBS} $(LIBINTL) -lbas -ldl

rootlib_LTLIBRARIES = \
    libexecfilter.la \
    libefstub.la

libexecfilter_la_SOURCES = \
    filter.h \
    filter.c \
    log.h \
    trie.h \
    trie.c \
    posix/exec.c \
    posix/popen.c \
    posix/posix_fn.h \
    posix/spawn.c \
    posix/system.c

libexecfilter_la_LIBADD = ${GLIB_LIBS} -lbas -ldl
libexecfilter_la_LDFLAGS = -version-info 1:0:0

libefstub_la_SOURCES = stub.c
libefstub_la_LDFLAGS = -version-info 1:0:0

dist_syslogconf_DATA = \
    10execfilter.conf

dist_doc_DATA = manual.pdf

manual.pdf: main.tex
	-cooltex -a -jmanual -T$(XELATEX) main.tex

dist_setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    config.rpath \
    main.tex \
    itree.tex

CLEANFILES = \
    ${dist_setup_SCRIPTS}
