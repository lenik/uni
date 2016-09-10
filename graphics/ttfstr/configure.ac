#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.69])
AC_INIT([ttfstr], [0.1], [xjl-bug@99jsj.com])
AM_INIT_AUTOMAKE([foreign -Wno-portability])

# Checks for programs.
AC_PROG_CC
AC_PROG_CC_STDC
AC_PROG_CXX
AC_PROG_LN_S

# Checks for libraries.
#AC_CHECK_LIB
AC_CONFIG_MACRO_DIR([m4])
#AX_BOOST_BASE([1.48],, [AC_MSG_ERROR([Required the boost library])])
#    AX_BOOST_SYSTEM
#    AX_BOOST_DATE_TIME
#    AX_BOOST_THREAD

PKG_CHECK_MODULES([GLIB], [glib-2.0])

PKG_CHECK_MODULES([OPENCV], [opencv >= 2.4.8])
    AC_SUBST(OPENCV_CFLAGS)
    AC_SUBST(OPENCV_LIBS)

PKG_CHECK_MODULES([FREETYPE2], [freetype2])
#    AC_SUBST(FREETYPE2_CFLAGS)
#    AC_SUBST(FREETYPE2_LIBS)

# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADER([unistd.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_CHECK_HEADER_STDBOOL
AC_TYPE_SIZE_T

# Checks for library functions.

AC_CONFIG_FILES([Makefile])
AC_CONFIG_FILES([src/Makefile])
AC_CONFIG_HEADERS([src/config.h])
AC_OUTPUT
