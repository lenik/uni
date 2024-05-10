#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.65])
AC_INIT([tclock], [0.1], [xjl-bug@99jsj.com])
AM_INIT_AUTOMAKE([-Wno-portability])

# Checks for programs.
    AC_PROG_CC
    AC_PROG_CXX
    AC_PROG_LN_S
    AC_PROG_RANLIB

# Checks for libraries.
    PKG_CHECK_MODULES([X11], [x11])
    PKG_CHECK_MODULES([XT], [xt])
    PKG_CHECK_MODULES([XFT], [xft])

# Checks for header files.

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.

AC_CONFIG_FILES([Makefile])
AC_CONFIG_HEADERS([config.h])
AC_OUTPUT
