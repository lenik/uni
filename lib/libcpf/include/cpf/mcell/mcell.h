
#include "stdhdrs.h"
#include <cpf/assert.h>
#include <cpf/const/bitinfo.h>

/* SIMXML PROJECT */

// mbuf : membuf.

/* WITHOUT Emacs... */
class allocator {
public:
    virtual void *allocate(size_t size) = 0;
    virtual void deallocate(void *) = 0;
};

class mcell_allocator : public allocator {
public:
    mcell_allocator(mcell_allocator *parent,
                    const int cell_size,
                    const int capacity,
                    allocator *bitmap_allocator,
                    u32_t flags)
            : parent(parent),
              cell_size(cell_size),
              capacity(capacity),
              bitmap_allocator(bitmap_allocator) {
        //
    }

    void *allocate(size_t size) {
        return 0;
    }
    void deallocate(void *p) {}
    void *allocate_cell();
    void *allocate_cells(int count);

private:
    mcell_allocator *parent;
    const int cell_size;
    const int capacity;
    allocator *bitmap_allocator;
    u32_t flags;

    u8_t *bitmap;
    size_t next_oct;
    u8_t next_mask;
};
