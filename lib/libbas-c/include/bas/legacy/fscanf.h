#ifndef __BAS_LEGACY_FSCANF
#define __BAS_LEGACY_FSCANF

#if __i386__
    __asm__(".symver fscanf_2_0,fscanf@GLIBC_2.0");
    #define fscanf fscanf_2_0
#else
    __asm__(".symver fscanf_2_2_5,fscanf@GLIBC_2.2.5");
    #define fscanf fscanf_2_2_5
#endif

#endif
