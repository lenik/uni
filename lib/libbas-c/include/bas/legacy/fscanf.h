#ifndef __BAS_LEGACY_FSCANF
#define __BAS_LEGACY_FSCANF

__asm__(".symver fscanf_2_0,fscanf@GLIBC_2.0");
#define fscanf fscanf_2_0

#endif
