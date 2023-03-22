include ../../../libauto/project.am

JAVA = $(shell readlink -f $$(which java))
JAVA_DIR = $(patsubst %/, %, $(dir $(JAVA)))
JAVA_HOME = $(patsubst %/bin, %, $(patsubst %/jre/bin, %, $(JAVA_DIR)))
JDK_INCLUDE = $(JAVA_HOME)/include
JDK_INCLUDE_NATIVE = $(JDK_INCLUDE)/linux

lib2dir = /usr/lib
lib2_LTLIBRARIES = libjnigen.la
libjnigen_la_LIBADD = 
libjnigen_la_LDFLAGS = -version-info 1:0:0
libjnigen_la_CXXFLAGS = \
    -I$(JDK_INCLUDE) \
    -I$(JDK_INCLUDE_NATIVE)

dist_libjnigen_la_SOURCES = \
    jnigen.cxx

incdir = ${prefix}/include

dist_inc_DATA = \
    jnigen.hxx

