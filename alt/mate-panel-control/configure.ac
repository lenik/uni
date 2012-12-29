#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.65])
AC_INIT([mate-panel-control], [0.1.1], [xjl-bug@99jsj.com])
AM_INIT_AUTOMAKE([-Wno-portability])

MATE_COMMON_INIT

# Checks for programs.
AC_PROG_LN_S

# Checks for libraries.
PKG_CHECK_MODULES(X, x11 xau, :, [
  # pkg-config modules not found (only present since X11R7 aka Xorg); use
  # old-style detection
  AC_PATH_XTRA()
  # X not found
  if test x$no_x = xyes ; then
    AC_MSG_ERROR([X development libraries not found])
  fi

  gp_save_cflags="$CFLAGS"
  gp_save_libs="$LIBS"
  CFLAGS="$X_CFLAGS"
  LIBS="$X_PRE_LIBS $X_LIBS $X_EXTRA_LIBS"
  AC_CHECK_LIB(X11, XFree,, AC_MSG_ERROR([libX11 not found]))
  AC_CHECK_LIB(Xau, XauFileName,, AC_MSG_ERROR([libXau not found]))
  CFLAGS="$gp_save_cflags"
  LIBS="$gp_save_libs"

  X_LIBS="$X_PRE_LIBS $X_LIBS -lX11 -lXau $X_EXTRA_LIBS"
])

AC_SUBST(X_LIBS)

# Checks for header files.

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
