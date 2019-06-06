include ../libauto/common.am

bashbuiltinsdir = ${libdir}

bashbuiltins_PROGRAMS = \
    bash-ex

dist_bash_ex_SOURCES = \
    strfn.c \
    math.c

bash_ex_CFLAGS = -fpic -I/usr/include/bash -I/usr/include/bash/include

bash-ex${EXEEXT}: ${am_bash_ex_OBJECTS}
	gcc -shared -lbas -o $@ $^
