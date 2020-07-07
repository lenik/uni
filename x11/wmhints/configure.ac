#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.65])
AC_INIT([wmhints], [0.1.1], [xjl-bug@99jsj.com])
AM_INIT_AUTOMAKE([-Wno-portability])
LT_INIT([disable-static])
#AC_CONFIG_MACRO_DIR([m4])
#AM_GNU_GETTEXT([external])                                                      

# Checks for programs.
    AC_PROG_CC
    AC_PROG_LN_S
    AC_PROG_RANLIB

# Checks for libraries.
    PKG_CHECK_MODULES([GLIB], [glib-2.0])
    AC_SUBST([GLIB_CFLAGS])
    AC_SUBST([GLIB_LIBS])

    PKG_CHECK_MODULES([X11], [x11])
    AC_SUBST([X11_CFLAGS])
    AC_SUBST([X11_LIBS])

# Checks for header files.
    AC_HEADER_STDC
    AC_CHECK_HEADERS([stdlib.h])

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.
    AC_CHECK_FUNCS([strchr])
    AC_CHECK_FUNCS([strtol])
    AC_FUNC_ERROR_AT_LINE

AC_CONFIG_FILES([Makefile])
AC_CONFIG_HEADER([config.h])
AC_OUTPUT
