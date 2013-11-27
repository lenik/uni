
#pragma once

#ifndef _CPF_START
#define _CPF_START

/*
    _CPF_START_
        NODISABLE_WARNINGS
        NOCOMUTIL           comutil.h comsupp.lib
        NOSTD
        NOMACROS
        NOINLINES           __elem, __reference
        NOASSERT            ASSERT, DBGPOP
        NOSP                (Safe procedure)
        NOSAFE              SAFE_RELEASE, SAFE_DELETE, SAFE_DELETE_ARRAY
*/


#ifndef _CPF_START_NODISABLE_WARNINGS
#   pragma warning(disable: 4100)       // unreferenced formal parameter
#   pragma warning(disable: 4127)       // condition is constant
#   pragma warning(disable: 4786)       // symbol long in debug info
#   pragma warning(disable: 4530)       // exception with /GX
#endif


#include <stdio.h>
#include <stdlib.h>
#include <tchar.h>
#include <windows.h>
#include <windowsx.h>
#include <commdlg.h>
#include <tlhelp32.h>

#ifndef _CPF_START_NOCOMUTIL
#   pragma comment(lib, "comsupp.lib")
#   include <comutil.h>
#endif

#include "../assert.h"
#include "../safeproc.h"
#include "../cpputil.h"

#ifndef _CPF_START_NOSTD
#   include <iostream>
#   include <strstream>
#   include <vector>
#   include <list>
#   include <utility>

#   define __std    using namespace std

    template<class _Ty>
    _Ty *__elem(std::list<_Ty>& lst, int index) {
        std::list<_Ty>::iterator it;
        if (index >= lst.size()) return NULL;
        for (it = lst.begin(); index > 0; index--) {
            ++it;
            if (it == lst.end()) {
                //__assume(false);
                return NULL;
            }
        }
        return &*it;
    }
#endif // _CPF_START_NOSTD

#ifndef _CPF_START_NOMACROS

#   ifndef _TS
#       define _TS(x)           (LPCTSTR)_bstr_t(x)
#   endif

#   ifndef __offset
#       define __offset_OFF_P           0
#       define __offset(cls, basecls)   ((int) ( static_cast<basecls *>((cls *)__offset_OFF_P) ) - __offset_OFF_P )
#   endif


#   ifndef _CPF_START_NOCOMUTIL
        inline int __reference(IUnknown *p) {
            p->AddRef();
            return p->Release();
        }
#   endif

#   define DEFINE_OBJ(i)                CComPtr<I##i> p##i;

#   ifndef _CPF_START_NOINLINES
        template<class RetT, class ParamT>
        inline RetT __cast(ParamT x) { return (RetT)x; }
#   endif

#   define FEIY_BEGIN                   namespace feiy {
#   define FEIY_END                     }

#   define WARN(uID)                    { feiy::popup_message((LPCTSTR)(uID), false); }
#   define FATAL(uID)                   { feiy::popup_message((LPCTSTR)(uID), true); }

#   define SYSWARN(errno)               { feiy::popup_errmsg(errno, false); }
#   define SYSERR(errno)                { feiy::popup_errmsg(errno, true); }
#   define SYSVERIFY(v)                 if (!(v)) SYSERR(GetLastError())

#   ifndef _CPF_START_NOINLINES
    FEIY_BEGIN
        inline void __stdcall popup_message(LPCTSTR title, bool fatal = false) {
            TCHAR   buffer[1024];
            TCHAR   sentence[1024];

            if ((LPARAM)title < 0x10000) {
                LoadString(GetModuleHandle(NULL), (UINT)(LPARAM)title, buffer, sizeof(buffer) / sizeof(TCHAR));
            } else {
                lstrcpy(buffer, title);
            }

            _stprintf(sentence, TEXT("Runtime Error: \n\n%s\n\nPlease restart the program or contact the system administrator. "),
                buffer);

            if (!fatal) {
            #ifndef __silence_message
                MessageBox(NULL, sentence, TEXT("CPF STARTER"), MB_OK | MB_ICONINFORMATION);
            #endif
            } else {
                MSG msg;
                UINT id;

                // we remove WM_QUIT because if it is in the queue then the message box
                // won't display
                BOOL bQuit = PeekMessage(&msg, NULL, WM_QUIT, WM_QUIT, PM_REMOVE);
                id = MessageBox(NULL, sentence, TEXT("START.INC FRAMEWORK"),
                    MB_ABORTRETRYIGNORE | MB_ICONERROR);
                if (bQuit) PostQuitMessage((int)msg.wParam);

                switch (id) {
                    case IDABORT:
                        if (!bQuit) exit(-1);
                        break;
                    case IDRETRY:
                        _asm int 3;
                        break;
                    case IDIGNORE: ;
                }
            }
        }

        inline void __stdcall popup_errmsg(DWORD errno, bool fatal = false) {
            LPTSTR lpMsgBuf;
            if (!FormatMessage(
                FORMAT_MESSAGE_ALLOCATE_BUFFER |
                FORMAT_MESSAGE_FROM_SYSTEM |
                FORMAT_MESSAGE_IGNORE_INSERTS,
                NULL,
                errno,
                MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
                (LPTSTR)&lpMsgBuf,
                0,
                NULL))
            {
                popup_message(TEXT("Unexpected: Error occured when FormatMessage!"), fatal);
            } else {
                popup_message(lpMsgBuf, fatal);
                // never executed:
                // LocalFree(lpMsgBuf);
            }
        }


#       ifndef _CPF_START_NOMODREF
#           ifdef _DEBUG
#               define _MOD_OBJECTS_    _g_mod_objects
#               define MLOCK            ++FEIY(_MOD_OBJECTS_)
#               define MUNLOCK          --FEIY(_MOD_OBJECTS_)
#               define DEFINE_MODREF                                                            \
                    FEIY_BEGIN                                                                  \
                    long _MOD_OBJECTS_;                                                         \
                    struct _refchecker_on_exit_ {                                               \
                        int _stuff;                                                             \
                        _refchecker_on_exit_() { _MOD_OBJECTS_ = 0; }                           \
                        ~_refchecker_on_exit_() {                                               \
                            if (_MOD_OBJECTS_ != 0) {                                           \
                                TCHAR buf[50];                                                  \
                                _stprintf(buf,                                                  \
                                    TEXT("ML-%d: Module was still locked by %d users!"),        \
                                    _MOD_OBJECTS_, _MOD_OBJECTS_);                              \
                                FATAL(buf);                                                     \
                            }                                                                   \
                        }                                                                       \
                    } _g_refchecker_on_exit_;                                                   \
                    FEIY_END
                extern long _MOD_OBJECTS_;
#           else
#               define _MOD_OBJECTS_    _f_local_objects
#               define MLOCK
#               define MUNLOCK
#               define DEFINE_MODREF
                static long _MOD_OBJECTS_ = 0;
#           endif
#       endif // _CPF_START_NOMODREF

    FEIY_END

#   endif // _CPF_START_NOINLINES

#endif // _CPF_START_NOMACROS

#endif // _CPF_START
