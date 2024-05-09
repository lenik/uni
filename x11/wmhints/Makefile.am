include libauto/project.am

ACLOCAL_AMFLAGS = -I m4
AM_CPPFLAGS = $(GLIB_CFLAGS) -DLOG_LEVEL=4
#AM_CFLAGS = -Wall -g -O0

#SUBDIRS = po test

bin_PROGRAMS = \
    window-decorations \
    wmhints

window_decorations_SOURCES = window-decorations.c
window_decorations_LDADD = ${X11_LIBS}

wmhints_SOURCES = wmhints.c wmhints.h
wmhints_LDADD = ${GLIB_LIBS} ${X11_LIBS} ${LIBINTL} -lbas

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}
