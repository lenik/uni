#ifndef _start_umac_n_xWDeYtXaeGwSrK_
#define _start_umac_n_xWDeYtXaeGwSrK_ "$Id: start.h,v 1.3 2006-05-03 00:47:39 dansei Exp $"

#include "config.h"

/* system-indepentent utilities */
#define _zinit_(v) memset(&(v), 0, sizeof(v))

#if CPF_PLATFORM_WIN32
#   include "win32/start.h"
#else
#   error OS other than Win32 is Not supported yet.
#endif

/* cpp utilities using c features */
#ifdef AfxDebugBreak
#    define _breakpoint_() AfxDebugBreak()
#else
#    define _breakpoint_() { __asm int 3 }
#endif

#endif
