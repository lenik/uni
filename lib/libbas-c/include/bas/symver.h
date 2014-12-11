#ifndef __BAS_SYMVER
#define __BAS_SYMVER

#define SYMVER(sym, lib, ver) \
    __asm__(".symver "#sym", "#sym"@"#lib"_"#ver); \
    __asm__(".symver __symver_"#sym", "#sym"@"#lib"_"#ver);

#endif
