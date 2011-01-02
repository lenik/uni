
#ifndef __NETCON_UTIL
#define __NETCON_UTIL

#define NETCON_CONNECT          1
#define NETCON_DISCONNECT       2

#include <cpf/dynasub.h>

DYNASUB("netcon.dll", netcon_do, HRESULT, _stdcall,
        (char *id, int method),
        (id, method))

DYNASUB("netcon.dll", test, int, _stdcall,
        (int a, int b),
        (a, b))

#endif
