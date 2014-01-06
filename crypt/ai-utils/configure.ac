#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.65])
AC_INIT([ai-utils], [2.1.2], [xjl-bug@99jsj.com])
#LT_INIT([disable-static])
#AC_CONFIG_MACRO_DIR([m4])
AM_INIT_AUTOMAKE([-Wno-portability])

# Checks for programs.
    AC_PROG_CC
    AC_PROG_LN_S
    AC_PROG_RANLIB

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
AC_CONFIG_HEADERS([config.h])
AC_OUTPUT
