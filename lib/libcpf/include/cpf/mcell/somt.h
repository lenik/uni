
mcell_allocator mcell_1K(0, 1024, 100, 0, 0);

allocator *somt_allocator = &mcell_1K;

/* Seg. Opt. Mapping-Table */

struct somt_entry {
    u32_t cc;
    void *ptr;
};

typedef somt_entry *somt_page;

#define CC_NULL -1
#define CC_MORE '.MOR'

inline void *somt_get(somt_page root, u32_t cc) {
    u8_t *pc = (u8_t *)&cc;
    /* Index from 0 because of Little-Endian */
    for (int i = 0; i < 4; i++) {
        somt_entry *entry = root + *pc;
        u32_t cur_cc = entry->cc;
        if (cur_cc == CC_NULL)
            return NULL;                /* not available */
        if (cur_cc == cc)
            return entry->ptr;          /* hit */
        if (cur_cc != CC_MORE)
            return NULL;                /* has been occupied by another cc */
        /* more */
        root = (somt_page)entry->ptr;
        pc++;
    }
    return NULL;
}

inline void somt_set(somt_page root, u32_t cc, void *ptr) {
    u8_t *pc = (u8_t *)&cc;
    for (int i = 0; i < 4; i++) {
        somt_entry *entry = root + *pc;
        u32_t cur_cc = entry->cc;
        if (cur_cc == CC_NULL) {        /* not available (existed) */
            entry->ptr = ptr;
        } else if (cur_cc == cc) {      /* hit (existed) */
            entry->ptr = ptr;
        } else if (cur_cc != CC_MORE) { /* has been occupied by another cc, do split */
            somt_entry old = *entry;
            somt_page new_page = (somt_page)somt_allocator->allocate(sizeof(somt_entry) * 256);
            memset(new_page, 0, sizeof(somt_entry) * 256);
            entry->cc = CC_MORE;
            entry->ptr = new_page;
            new_page[old.cc] = old;
            entry = new_page + *pc;
            entry->cc = cc;
            entry->ptr = ptr;
            return;
        }
        /* more */
        root = (somt_page)entry->ptr;
        pc++;
    }
}
