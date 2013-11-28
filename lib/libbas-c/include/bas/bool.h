#ifndef __BAS_BOOL_H
#define __BAS_BOOL_H

typedef _Bool bool;

enum {
    false = 0,
    true = 1
};

#ifndef true
#define true true
#endif

#ifndef false
#define false false
#endif

#endif
