#include "EXTERN.h"
#include "perl.h"
#include "XSUB.h"
#include "ppport.h"
#include <xsutil.h>
#include <IPHlpApi.h>
#define DEBUG
#include <cpf/assert.h>
#define DYNALOAD
#include "netcon/netcon.h"

typedef BYTE BYTE_MAX_ADAPTER_ADDRESS_LENGTH        [MAX_ADAPTER_ADDRESS_LENGTH];
typedef char char_MAX_ADAPTER_NAME_LENGTH_4         [MAX_ADAPTER_NAME_LENGTH + 4];
typedef char char_MAX_ADAPTER_DESCRIPTION_LENGTH_4  [MAX_ADAPTER_DESCRIPTION_LENGTH + 4];

MODULE = Net::Adapter		PACKAGE = Net::Adapter

INCLUDE: Adapter.typemap.xsh
## TYPEMAP(PIP_ADAPTER_INFO)

int
enum_adapters(callback)
        SV *callback
    PROTOTYPE: $
    INIT:
        DWORD   ret;
        PIP_ADAPTER_INFO pInfo;
        ULONG   cb;
        // ULONG   cb1;
        // int     count = 0;
    CODE:
        _verify_(GetAdaptersInfo(0, &cb) == ERROR_BUFFER_OVERFLOW);
        // cb1 = sizeof(IP_ADAPTER_INFO);
        // if (sizeof(time_t) == 8) cb1 -= 8;
        // count = cb / cb1;
        pInfo = (PIP_ADAPTER_INFO) malloc(cb);
        ret = GetAdaptersInfo(pInfo, &cb);
        if (ret == ERROR_SUCCESS) {
            PIP_ADAPTER_INFO next = pInfo;
            while (next) {
                SV *sv = TYPEMAP_OUT(next, PIP_ADAPTER_INFO, "Net::Adapter");
                PUSHMARK(SP);
                    XPUSHs(sv);
                PUTBACK;
                    call_sv(callback, G_DISCARD);
                SPAGAIN;
                next = next->Next;
            }
        }
        free(pInfo);
        RETVAL = ret;
    OUTPUT:
        RETVAL

int
netcon_connect(id)
        char *id
    PROTOTYPE: $
    CODE:
        RETVAL = netcon_do(id, NETCON_CONNECT);
    OUTPUT:
        RETVAL

int
netcon_disconnect(id)
        char *id
    PROTOTYPE: $
    CODE:
        RETVAL = netcon_do(id, NETCON_DISCONNECT);
    OUTPUT:
        RETVAL

INCLUDE: Adapter.class.xsh
