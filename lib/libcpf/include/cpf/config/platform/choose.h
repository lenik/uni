#ifndef _choose_umac_n_xWDeY_BEjht87g_
#define _choose_umac_n_xWDeY_BEjht87g_ "$Id: choose.h,v 1.3 2006-05-03 00:47:39 dansei Exp $"

#if defined (__PVM__)
#    define CPF_PLATFORM "POS VM"
#    define CPF_PLATFORM_PVM
#    include "pvm.h"
#elif defined(__IBMCPP__) || defined(_AIX)
#    define CPF_PLATFORM "IBM AIX"
#    define CPF_PLATFORM_AIX
#    include "aix.h"
#elif defined(__amigaos__)
#    define CPF_PLATFORM "Amiga OS"
#    define CPF_PLATFORM_AMIGA
#    include "amiga.h"
#elif defined(__BEOS__)
#    define CPF_PLATFORM "BeOS"
#    define CPF_PLATFORM_BEOS
#    include "beos.h"
#elif defined(__FreeBSD__) || defined(__NetBSD__) || defined(__OpenBSD__) || defined(__DragonFly__)
#    define CPF_PLATFORM "BSD"
#    define CPF_PLATFORM_BSD
#    include "bsd.h"
#elif defined(__CYGWIN__)
#    define CPF_PLATFORM "Cygwin"
#    define CPF_PLATFORM_CYGWIN
#    include "cygwin.h"
#elif defined(__hpux)
#    define CPF_PLATFORM "HP Unix"
#    define CPF_PLATFORM_HPUX
#    include "hpux.h"
#elif defined(__sgi)
#    define CPF_PLATFORM "SGI Irix"
#    define CPF_PLATFORM_IRIX
#    include "irix.h"
#elif defined(linux) || defined(__linux) || defined(__linux__)
#    define CPF_PLATFORM "Linux"
#    define CPF_PLATFORM_LINUX
#    include "linux.h"
#elif defined(macintosh) || defined(__APPLE__) || defined(__APPLE_CC__)
#    define CPF_PLATFORM "Apple MacOS"
#    define CPF_PLATFORM_MAC
#    include "mac.h"
#elif defined(__QNXNTO__)
#    define CPF_PLATFORM "QNX"
#    define CPF_PLATFORM_QNX
#    include "qnx.h"
#elif defined(sun) || defined(__sun)
#    define CPF_PLATFORM "Sun Solaris"
#    define CPF_PLATFORM_SOLARIS
#    include "solaris.h"
#elif defined(_WIN32) || defined(__WIN32__) || defined(WIN32)
#    define CPF_PLATFORM "Microsoft Win32"
#    define CPF_PLATFORM_WIN32
#    include "win32.h"
#elif defined(unix) || defined(__unix) || defined(_XOPEN_SOURCE) || defined(_POSIX_SOURCE)
#    define CPF_PLATFORM "Generic Unix"
#    define CPF_PLATFORM_UNIX
#    include "unix.h"
#else
#    error "Unknown platform"
#endif

#endif
