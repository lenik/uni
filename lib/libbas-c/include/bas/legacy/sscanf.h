#ifndef __BAS_LEGACY_SSCANF
#define __BAS_LEGACY_SSCANF

#if __i386__
    __asm__(".symver sscanf_2_0,sscanf@GLIBC_2.0");
    #define sscanf sscanf_2_0
#else
    __asm__(".symver sscanf_2_2_5,sscanf@GLIBC_2.2.5");
    #define sscanf sscanf_2_2_5
#endif

#endif
