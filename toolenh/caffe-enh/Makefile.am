include libauto/project.am

ACLOCAL_AMFLAGS = -I m4

AM_CPPFLAGS = \
    $(BOOST_CPPFLAGS) \
    $(GLIB_CFLAGS) \
    -DUSE_OPENCV

AM_CXXFLAGS = \
    -std=c++14 \
    @OPENCV_CFLAGS@

AM_LDFLAGS = \
    $(BOOST_LDFLAGS) \
    @OPENCV_LIBS@ \
    @GLIB_LIBS@ \
    -lcaffe \
    -lglog \
    -lbas \
    -llmdb

bin_PROGRAMS = \
    cclass

cclass_SOURCES = \
    cclass.cc \
    classifier.cc \
    fn.cc

cclass_LDADD = \
    -lboost_system
    #$(BOOST_SYSTEM_LIB)

dist_setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = bootstrap

CLEANFILES = \
    ${dist_setup_SCRIPTS}
