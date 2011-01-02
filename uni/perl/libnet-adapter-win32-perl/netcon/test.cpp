
#include <stdio.h>
#include <windows.h>
#include <cpf/start/win32.h>

#include <netcon.h>

// #define DYNALOAD
#include "netcon.h"

#pragma comment(lib, "uuid.lib")

int main() {
    int c;
    DWORD ret;

    /*
    const char *sid;
    LPOLESTR pws = 0;
    StringFromCLSID(CLSID_ConnectionManager, &pws);
    sid = _TS(pws);
    CoTaskMemFree(pws);
    printf("sid=%s\n", sid);
    return 0;
    */

    c = test(3, 4);
    printf("C=%d\n", c);

    ret = netcon_do("F3F4AE2D-B300-440E-A6A1-EDF01591CCED", NETCON_CONNECT);
    return 0;
}
