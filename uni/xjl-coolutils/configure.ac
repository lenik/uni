#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.65])
AC_INIT([xjl-coolutils], [0.1], [xjl-bug@99jsj.com])
AM_INIT_AUTOMAKE([-Wall])
AC_CONFIG_SRCDIR([uc.c])

# Checks for programs.
AC_PROG_LN_S

AC_PROG_CC

# Checks for libraries.

# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADERS([unistd.h])
AC_CHECK_HEADERS([string.h])
AC_CHECK_HEADERS([time.h])

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.
AC_CHECK_FUNCS([strrchr strtol])

AC_CONFIG_FILES([Makefile])
AC_CONFIG_HEADERS([config.h])
AC_OUTPUT
