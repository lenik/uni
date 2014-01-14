include ../libauto/common.am

bashbuiltinsdir = ${libdir}

bashbuiltins_PROGRAMS = \
    bash-ex

dist_bash_ex_SOURCES = \
    strfn.c

bash_ex_CFLAGS = -fpic -I/usr/include/bash

bash-ex${EXEEXT}: ${am_bash_ex_OBJECTS}
	ld -shared -lbas -o $@ $^

