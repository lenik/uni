#include <stdio.h>
#include <stdlib.h>

#include <windows.h>
#include <shobjidl.h>
#include <shlguid.h>

void check(BOOL pred, const char *mesg);
void help();
HRESULT CreateLink(LPCSTR lpszPathObj, LPCSTR lpszPathLink);

char *opt_ext = "lnk";
int opt_force = 0;

int main(int argc, char **argv) {
    argc--;
    argv++;
    while (argc > 0) {
        char *arg = *argv;
        int _breakL = 0;
        if (*arg == '-') {
            switch (*++arg) {
            case 'x':                   /* name */
                check(--argc, "-x EXTNAME");
                opt_ext = _strdup(*++argv);
                break;
            case 'f':                   /* wait */
                opt_force = 1;
                break;
            default:
                // break L;
                _breakL = 1;
            }
        } else
            break;
        if (_breakL)
            break;
        argc--;
        argv++;
    }

    check(argc >= 2, "SOURCE and DEST must be specified. ");

    CoInitialize(NULL);

    char dst[MAX_PATH];
    sprintf(dst, "%s.%s", argv[1], opt_ext);
    // printf("src=%s\n", argv[0]);
    // printf("dst=%s\n", dst);
    HRESULT hr = CreateLink(argv[0], dst);
    if (FAILED(hr))
        printf("Error: %08x\n", hr);

    CoUninitialize();
    return (int) hr;
}

void check(BOOL pred, const char *mesg) {
    if (pred)
        return;
    fprintf(stderr, "error: %s\n", mesg);
    exit(1);
}

void help() {
    printf("lnk [OPTIONS] SOURCE DEST\n"
           "\n"
           "Options: \n"
           "  -x EXTNAME       default .lnk\n"
           "  -f               overwrite the existing\n"
           );
}

HRESULT CreateLink(LPCSTR lpszPathObj, LPCSTR lpszPathLink)
{
    HRESULT hres;
    IShellLink* psl;

    // Get a pointer to the IShellLink interface.
    //hres = CoCreateInstance(__uuidof(ShellLink), NULL, CLSCTX_INPROC_SERVER,
    //                        __uuidof(IShellLink), (LPVOID*)&psl);
    hres = CoCreateInstance(CLSID_ShellLink, NULL, CLSCTX_INPROC_SERVER,
                            IID_IShellLink, (LPVOID*)&psl);
    if (SUCCEEDED(hres))
    {
        IPersistFile* ppf;

        // Set the path to the shortcut target and add the description.
        // psl->SetPath(lpszPathObj);
        psl->SetRelativePath(lpszPathObj, 0);
        // psl->SetDescription(lpszDesc);

        // Query IShellLink for the IPersistFile interface for saving the
        // shortcut in persistent storage.
        hres = psl->QueryInterface(IID_IPersistFile, (LPVOID*)&ppf);

        if (SUCCEEDED(hres))
        {
            WCHAR wsz[MAX_PATH];

            // Ensure that the string is Unicode.
            MultiByteToWideChar(CP_ACP, 0, lpszPathLink, -1, wsz, MAX_PATH);

            // TODO: Check return value from MultiByteWideChar to ensure success.

            // Save the link by calling IPersistFile::Save.
            hres = ppf->Save(wsz, TRUE);
            ppf->Release();
        }
        psl->Release();
    }
    return hres;
}
