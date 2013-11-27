#ifndef _qsort_utim_c7d3UgWqQgRddz34_
#define _qsort_utim_c7d3UgWqQgRddz34_ "$Id: qsort_.h,v 1.4 2006-05-15 15:14:54 dansei Exp $"

#include <cpf/config.h>
#include <cpf/dt/raw.h>

typedef int (_cc *qsort__cmpf_t)(const void *, const void *, void *);

void _cc
qsort_(void *base, size_t num, size_t width, qsort__cmpf_t cmpf,
       void *user);

#endif
