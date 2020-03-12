include libauto/project.am

AM_CPPFLAGS = ${GLIB_CFLAGS}
CatMe_LDADD = ${GLIB_LIBS}

bin_PROGRAMS = \
    CatMe

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

