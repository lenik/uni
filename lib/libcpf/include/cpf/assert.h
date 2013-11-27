#ifndef _assert_umac_n_xWDeYbyiZnwUnx_
#define _assert_umac_n_xWDeYbyiZnwUnx_ "$Id: assert.h,v 1.2 2006-05-02 00:15:07 dansei Exp $"

#include "config.h"

#ifdef _DEBUG
#   include <assert.h>
#   ifndef _assert_
#       define _assert_(x) assert(x)
#   endif
#   ifndef _verify_
#       define _verify_(x) assert(x)
#   endif
#   ifndef _debug_
#       define _debug_(x) (x)
#   endif
#else
#   ifndef _assert_
#       define _assert_(x)
#   endif
#   ifndef _verify_
#       define _verify_(x) (x)
#   endif
#   ifndef _debug_
#       define _debug_(x)
#   endif
#endif

#endif
