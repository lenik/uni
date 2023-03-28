include libauto/project.am

ACLOCAL_AMFLAGS = -I m4
AUTOMAKE_OPTIONS = subdir-objects

JAVA = $(shell readlink -f $$(which java))
JAVA_DIR = $(patsubst %/, %, $(dir $(JAVA)))
JAVA_HOME = $(patsubst %/bin, %, $(patsubst %/jre/bin, %, $(JAVA_DIR)))
JDK_INCLUDE = $(JAVA_HOME)/include
JDK_INCLUDE_NATIVE = $(JDK_INCLUDE)/linux

dist_include_HEADERS = \
    src/jnigen.hxx

lib_LTLIBRARIES = libsweetjni.la
lib_LIBRARIES = libsweetjni.a

AM_LDFLAGS = -version-info 1:0:0
AM_CXXFLAGS = \
    -fPIC \
    -I$(JDK_INCLUDE) \
    -I$(JDK_INCLUDE_NATIVE)
AM_LIBADD =

dist_libsweetjni_a_SOURCES = \
    src/jnigen.cxx
dist_libsweetjni_la_SOURCES = ${dist_libsweetjni_a_SOURCES}

libsweetjni_a_CXXFLAGS = ${AM_CXXFLAGS}
libsweetjni_la_CXXFLAGS = ${AM_CXXFLAGS}

libsweetjni_a_LIBADD = ${AM_LIBADD}
libsweetjni_la_LIBADD = ${AM_LIBADD}

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-debug:
	sudo ln -snf $$PWD/src/jnigen.hxx /usr/include/
	sudo ln -snf $$PWD/.libs/libsweetjni.so /usr/lib/libsweetjni.so.1.0.0

autobuild:
	autorun -r src -c 'makelook'

