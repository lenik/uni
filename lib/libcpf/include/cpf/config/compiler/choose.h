#ifndef _choose_umac_n_xWDeYOfGKzwlde_
#define _choose_umac_n_xWDeYOfGKzwlde_ "$Id: choose.h,v 1.2 2006-05-02 13:59:01 dansei Exp $"

#if defined(__Ca__)
#    define CPF_COMPILER "Calcium C*"
#    define CPF_COMPILER_CALCIUM
#    include "calcium.h"
#elif defined(__BORLANDC__)
#    define CPF_COMPILER "Borland C++"
#    define CPF_COMPILER_BORLANDC
#    include "borlandc.h"
#elif defined(__COMO__)
#    define CPF_COMPILER "Comeau C++"
#    define CPF_COMPILER_COMO
#    include "como.h"
#elif defined(__DECCXX)
#    define CPF_COMPILER "Compaq Tru64 Unix cxx"
#    define CPF_COMPILER_DECCXX
#    include "deccxx.h"
#elif defined(__DMC__)
#    define CPF_COMPILER "Digital Mars C++"
#    define CPF_COMPILER_DMC
#    include "dmc.h"
#elif defined(__ghs)
#    define CPF_COMPILER "Greenhills C++"
#    define CPF_COMPILER_GHS
#    include "ghs.h"
#elif defined(__GNUC__)
#    define CPF_COMPILER "GNU C++"
#    define CPF_COMPILER_GNUC
#    include "gnuc.h"
#elif defined(__HP_aCC)
#    define CPF_COMPILER "HP aCC"
#    define CPF_COMPILER_HPACC
#    include "hpacc.h"
#elif defined(__IBMCPP__)
#    define CPF_COMPILER "IBM Visual Age"
#    define CPF_COMPILER_IBMCPP
#    include "ibmcpp.h"
#elif defined (__ICC) || defined(__ICL) || defined(__ECC)
#    define CPF_COMPILER "Intel C++"
#    define CPF_COMPILER_INTEL
#    include "intel.h"
#elif defined (__INTEL_COMPILER)
#    define CPF_COMPILER "Intel C++"
#    define CPF_COMPILER_INTEL
#    include "intel.h"
#elif defined(__KCC)
#    define CPF_COMPILER "Kai C++"
#    define CPF_COMPILER_KCC
#    include "kcc.h"
#elif defined (__MRC__) || defined(__SC__)
#    define CPF_COMPILER "MPW MrC++ or SC++"
#    define CPF_COMPILER_MPW
#    define _SCxx
#    include "mpw.h"
#elif defined(_MSC_VER)
#    define CPF_COMPILER "Microsoft (Visual) C/C++"
#    define CPF_COMPILER_MSC
#    include "msc.h"
#elif defined(__MWERKS__)
#    define CPF_COMPILER "Metrowerks CodeWarrior"
#    define CPF_COMPILER_MWERKS
#    include "mwerks.h"
#elif defined(__sgi)
#    define CPF_COMPILER "SGI MIPS pro C++"
#    define CPF_COMPILER_SGI
#    include "sgi.h"
#elif defined(__SUNPRO_CC)
#    define CPF_COMPILER "Sun Workshop Compiler C++"
#    define CPF_COMPILER_SUNPROCC
#    include "sunprocc.h"
#elif defined(__ANSIC__)
#    define CPF_COMPILER "ANSI C"
#    define CPF_COMPILER_ANSIC
#    include "ansic.h"
#else
#    error "Unknown compiler"
#endif

#endif
