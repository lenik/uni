#ifndef _buffer_utim_c7Q9SKNdrPidPwJP_
#define _buffer_utim_c7Q9SKNdrPidPwJP_ "$Id: buffer.h,v 1.5 2006-05-15 15:14:54 dansei Exp $"

#include <cpf/config.h>
#include <cpf/dt/raw.h>

typedef struct _buffer_t buffer_t;

struct _buffer_t {
    u8_t *data;
    size_t size;
    size_t alloc;
    size_t growth;
};

buffer_t * _cc
buffer_new(void *data, size_t size, size_t alloc, size_t growth);

buffer_t * _cc
buffer_append(buffer_t *buffer, const void *data, size_t size);

buffer_t * _cc
buffer_prepend(buffer_t *buffer, const void *data, size_t size);

buffer_t * _cc
buffer_insert(buffer_t *buffer, size_t index, const void *data, size_t size);

void * _cc
buffer_free(buffer_t *buffer, int free_data);

void * _cc
buffer_data(buffer_t *buffer, size_t offset);

void * _cc
buffer_dataend(buffer_t *buffer, int offset);

#define buffer_appendv(buf, t, v) \
    (*(t *)buffer_dataend(buffer_append((buf), 0, sizeof(t)), \
                          -sizeof(t)) \
        = (t)(v))

#define buffer_prependv(buf, t, v) \
    (*(t *)(buffer_prepend((buf), 0, sizeof(t))->data) \
        = (t)(v))

#endif
