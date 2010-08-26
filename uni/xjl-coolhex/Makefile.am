
include libauto/subst.am

AM_CPPFLAGS = $(GLIB_CFLAGS)
CFLAGS = -O0 -g

bin_PROGRAMS = \
    sedx

dist_bin_SCRIPTS = \
    xdiff \
    xpatch

dist_man_MANS = \
    sedx.1 \
    xdiff.1 \
    xpatch.1

sedx_LDADD = $(GLIB_LIBS)
