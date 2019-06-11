include libauto/project.am

AM_CPPFLAGS = ${GLIB_CFLAGS}
catme_LDADD = ${GLIB_LIBS}

bin_PROGRAMS = \
    catme

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

