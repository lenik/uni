#ifndef _list_utim_c6OdoyATHaEjqss5_
#define _list_utim_c6OdoyATHaEjqss5_ "$Id: list.h,v 1.9 2006-05-15 15:14:54 dansei Exp $"

#include <cpf/config.h>
#include <cpf/dt/raw.h>

typedef struct _list_t list_t;

struct _list_t {
    list_t *next;
    list_t *prev;
    size_t size;                        /* if size=0, x32_t is used */
    /* char data[]; ... */
    x32_t user;
};

typedef int (_cc *mem_cmpf_t)(const void *, size_t, const void *, size_t,
                              void *);

size_t   _cc  list_size(list_t *list);
list_t * _cc list_first(list_t *list);
list_t * _cc list_last(list_t *list);
list_t * _cc list_sibling(list_t *list, int index);
list_t * _cc list_insert(list_t *list, const void *data, size_t size);
list_t * _cc list_new(const void *data, size_t size);
list_t * _cc list_append(list_t *list, const void *data, size_t size);
list_t * _cc list_detach(list_t **list);
list_t * _cc list_delete(list_t *list);
list_t * _cc list_push(list_t *list, const void *data, size_t size);
list_t * _cc list_unshift(list_t *list, const void *data, size_t size);
list_t * _cc list_pop(list_t *list);
list_t * _cc list_pop_detached(list_t *list, list_t **detached);
list_t * _cc list_shift(list_t *list);
list_t * _cc list_shift_detached(list_t *list, list_t **detached);
list_t * _cc list_copy(list_t *list);
list_t * _cc list_sort(list_t *list, mem_cmpf_t mcmpf, void *user);
list_t * _cc list_add_sorted(list_t *list, const void *data, size_t size,
                             mem_cmpf_t cmpf, void *user);

list_t * _cc list_free(list_t *list);

#define list_data(l) ((void *)&(((const list_t *)(l))->user))
#define list_data_(l, t) ((t)list_data(l))
#define list_getv(l) (((const list_t *)(l))->user.cp)
#define list_getv_(l, t) ((t)list_getv(l))
#define list_setv(l, v) (((list_t *)(l))->user.cp = (const void *)(v))
#define list_insertv(l, v) list_insert((l), (const void *)(v), 0)
#define list_appendv(l, v) list_append((l), (const void *)(v), 0)
#define list_pushv(l, v) list_push((l), (const void *)(v), 0)
#define list_unshiftv(l, v) list_unshift((l), (const void *)(v), 0)
#define list_add_sortedv(l, v, f, u) \
    list_add_sorted((l), (const void *)(v), 0, (f), (u))

#endif
