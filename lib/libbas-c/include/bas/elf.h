#ifndef __BAS_ELF_H
#define __BAS_ELF_H
 
#define SYMVER(fn, lib, ver) \
    __asm__(".symver " #fn "," #fn "@" #lib "_" #ver)
 
#define SYMVER_GLIBC(fn, ver) SYMVER(fn, "GLIBC", ver)

#endif
