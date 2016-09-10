include ../libauto/common.am

AM_CPPFLAGS = \
    $(BOOST_CPPFLAGS) \
    $(GLIB_CFLAGS) \
    $(FREETYPE2_CFLAGS) \
    -DUSE_OPENCV

AM_CXXFLAGS = \
    -std=c++14 \
    @OPENCV_CFLAGS@

AM_LDFLAGS = \
    $(BOOST_LDFLAGS) \
    @OPENCV_LIBS@ \
    @GLIB_LIBS@ \
    @FREETYPE2_LIBS@ \
    -lcaffe \
    -lglog \
    -lbas \
    -llmdb

bin_PROGRAMS = \
    ttfstr

ttfstr_SOURCES = \
    fn.cc \
    TFont.cc \
    TGlyph.cc \
    TLibrary.cc \
    ttfstr.cc
