#ifndef __BAS_COMPAT_SCANF
#define __BAS_COMPAT_SCANF

#include "../symver.h"

#if __i386__
    SYMVER(sscanf,  GLIBC, 2.0)
    SYMVER(scanf,   GLIBC, 2.0)
    SYMVER(swscanf, GLIBC, 2.0)
    SYMVER(vfwscanf,GLIBC, 2.0)
    SYMVER(vssscanf,GLIBC, 2.0)
    SYMVER(wsscanf, GLIBC, 2.0)
    SYMVER(fscanf,  GLIBC, 2.0)
    SYMVER(vswscanf,GLIBC, 2.0)
    SYMVER(vwscanf, GLIBC, 2.0)
    SYMVER(vfsscanf,GLIBC, 2.0)
    SYMVER(vscanf,  GLIBC, 2.0)
    SYMVER(fwsscanf,GLIBC, 2.0)
#else
    SYMVER(sscanf,  GLIBC, 2.2.5)
    SYMVER(scanf,   GLIBC, 2.2.5)
    SYMVER(swscanf, GLIBC, 2.2.5)
    SYMVER(vfwscanf,GLIBC, 2.2.5)
    SYMVER(vssscanf,GLIBC, 2.2.5)
    SYMVER(wsscanf, GLIBC, 2.2.5)
    SYMVER(fscanf,  GLIBC, 2.2.5)
    SYMVER(vswscanf,GLIBC, 2.2.5)
    SYMVER(vwscanf, GLIBC, 2.2.5)
    SYMVER(vfsscanf,GLIBC, 2.2.5)
    SYMVER(vscanf,  GLIBC, 2.2.5)
    SYMVER(fwsscanf,GLIBC, 2.2.5)
#endif

#define sscanf      __symver_sscanf
#define scanf       __symver_scanf
#define swscanf     __symver_swscanf
#define vfwscanf    __symver_vfwscanf
#define vssscanf    __symver_vssscanf
#define wsscanf     __symver_wsscanf
#define fscanf      __symver_fscanf
#define vswscanf    __symver_vswscanf
#define vwscanf     __symver_vwscanf
#define vfsscanf    __symver_vfsscanf
#define vscanf      __symver_vscanf
#define fwsscanf    __symver_fwsscanf

#endif
