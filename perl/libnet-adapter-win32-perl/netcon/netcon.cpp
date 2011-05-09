
#include <stdio.h>
#include <windows.h>
#include <iphlpapi.h>
#include <netcon.h>
#include <cpf/assert.h>

#include "netcon.h"

struct __declspec(uuid("{BA126AD1-2166-11D1-B1D0-00805FC1270E}")) _ConnectionManager
    { };

#define WIN32CODE(hr) ( \
    (HRESULT_FACILITY(hr) == FACILITY_WINDOWS || \
     HRESULT_FACILITY(hr) == FACILITY_WIN32) \
    ? HRESULT_CODE(hr) : (hr) )

#define RETRY(hr) ((hr) != S_OK && WIN32CODE(hr) == ERROR_RETRY)


static BOOL     inited      = FALSE;
static HMODULE  hmNetShell  = NULL;
// NcFreeNetconProperties
static void (__stdcall *ncfreep)(NETCON_PROPERTIES* pProps);

static BOOL init() {
    if (inited)
        return TRUE;
    if (! (hmNetShell = LoadLibrary("netshell.dll")))
        return FALSE;
    ncfreep = (void (__stdcall *)(NETCON_PROPERTIES *))
        GetProcAddress(hmNetShell, "NcFreeNetconProperties");
    if (ncfreep != 0) {
        inited = TRUE;
        return TRUE;
    }
    FreeLibrary(hmNetShell);
    return FALSE;
}

static void uninit() {
    if (! inited)
        return;
    FreeLibrary(hmNetShell);
}


HRESULT _stdcall netcon_do(char *id, int method) {
    HRESULT                 hr;
    char *                  pc;
    GUID                    guid;
    INetConnectionManager*  pConnMgr = 0;
    IEnumNetConnection*     pEnum = 0;
    INetConnection*         pCon = 0;
    ULONG                   count;

    if (! init())
        return E_FAIL;

    if (*id == '{') ++id;
    pc = id + strlen(id) - 1;
    if (*pc == '}') *pc = '\0';

    if (FAILED(UuidFromString((unsigned char *)id, &guid)))
        return E_INVALIDARG;

    CoInitialize(0);

    hr = CoCreateInstance(__uuidof(_ConnectionManager), 0, CLSCTX_ALL,
                          __uuidof(INetConnectionManager),
                          (void **)&pConnMgr);
    if (FAILED(hr))
        return hr;

    hr = pConnMgr->EnumConnections(NCME_DEFAULT, &pEnum);

    if (FAILED(hr)) {
        pConnMgr->Release();
        return hr;
    }

    while (pEnum->Next(1, &pCon, &count) == S_OK) {
        NETCON_PROPERTIES *pProps = 0;
        hr = pCon->GetProperties(&pProps);
        if (SUCCEEDED(hr)) {
            if (IsEqualGUID(guid, pProps->guidId)) {
                int retry = 3;

                switch (method) {
                case NETCON_CONNECT:
                    do { hr = pCon->Connect(); }
                        while (RETRY(hr) && --retry);
                    break;
                case NETCON_DISCONNECT:
                    do { hr = pCon->Disconnect(); }
                        while (RETRY(hr) && --retry);
                    break;
                default:
                    hr = E_INVALIDARG;
                    break;
                }
                // NcFreeNetconProperties(pProps);
                ncfreep(pProps);
                break;
            } // if guid-eq
            // NcFreeNetconProperties(pProps);
            ncfreep(pProps);
        } // if getProp
    } // pEnum->Next

    pEnum->Release();
    pConnMgr->Release();
    CoUninitialize();
    uninit();
    return SUCCEEDED(hr) ? 0 : hr;
}

int _stdcall test(int a, int b) {
    int c = a + b;
    printf("Test add %d + %d => %d\n", a, b, c);
    return c;
}
