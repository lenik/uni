#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.65])
AC_INIT([caffe-cpuonly], [0.1.2], [xjl-bug@99jsj.com])
AM_INIT_AUTOMAKE([-Wno-portability])

# Checks for programs.
AC_PROG_LN_S
AC_PROG_CC
AC_PROG_CXX

# Checks for libraries.

# Checks for header files.
AC_CHECK_HEADERS([stdint.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_CHECK_HEADER_STDBOOL
AC_C_INLINE
AC_TYPE_INT8_T
AC_TYPE_SIZE_T
AC_TYPE_UINT64_T

# Checks for library functions.
AC_FUNC_MALLOC
AC_CHECK_FUNCS([memset pow sqrt])

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
