#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.65])
AC_INIT([xjl-autover], [0.1.33], [xjl@99jsj.com])
AM_INIT_AUTOMAKE([-Wall])
AC_CONFIG_SRCDIR([vercomp.c])

# Checks for programs.
AC_PROG_CC
AC_PROG_LN_S

# Checks for libraries.
PKG_CHECK_MODULES([GLIB], [glib-2.0])

# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADERS([stdlib.h])

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.
AC_CHECK_FUNCS([strchr])
AC_CHECK_FUNCS([strtol])
AC_FUNC_ERROR_AT_LINE


AC_CONFIG_FILES([Makefile])
AC_CONFIG_FILES([lib/Makefile])
AC_CONFIG_HEADERS([config.h])
AC_OUTPUT
