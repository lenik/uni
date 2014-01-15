#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.65])
AC_INIT([execfilter], [1.4.4], [xjl-bug@99jsj.com])
AM_INIT_AUTOMAKE([-Wno-portability subdir-objects])
LT_INIT([disable-static])
AC_CONFIG_MACRO_DIR([m4])
AM_GNU_GETTEXT

# Checks for programs.
AC_PROG_CC
AC_PROG_LN_S

# Checks for libraries.
PKG_CHECK_MODULES([GLIB], [glib-2.0])
AC_SUBST([GLIB_CFLAGS])
AC_SUBST([GLIB_LIBS])

# Checks for header files.
AC_HEADER_STDC
#AC_CHECK_HEADER([unistd.h])

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.

AC_CONFIG_FILES([Makefile test/Makefile po/Makefile.in])
AC_CONFIG_HEADER([config.h])
AC_OUTPUT
