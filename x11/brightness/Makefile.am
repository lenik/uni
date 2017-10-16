include libauto/project.am

AM_CPPFLAGS = ${GLIB_CFLAGS}
brightness_LDADD = $(GLIB_LIBS)

bin_PROGRAMS = \
    brightness

dist_man_MANS = \
    brightness.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}
