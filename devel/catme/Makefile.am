include libauto/project.am

AM_CPPFLAGS = ${GLIB_CFLAGS}
catme_LDADD = ${GLIB_LIBS}

bin_PROGRAMS = \
    catme

catme_SOURCES = CatMe.c Type.c
catme_LDADD = $(GLIB_LIBS) -lbas

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}
