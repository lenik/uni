#ifndef _start_umac_n_xWDeY4wnHke7bu_
#define _start_umac_n_xWDeY4wnHke7bu_ "$Id: start.hh,v 1.3 2006-05-03 00:47:39 dansei Exp $"

#include "config.h"
#include "start.h"

#if CPF_PLATFORM_WIN32
#   include "win32/start.h"
#else
#   error OS other than Win32 is Not supported yet.
#endif

/* cpp utilities using c++ features */
#include "specfunc.hh"

#endif
