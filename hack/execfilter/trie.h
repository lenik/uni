#ifndef __TRIE_H
#define __TRIE_H

#include <glib.h>

/* The key type of the trie node is an integer. */
#define TRIE_KEY_HASH g_int_hash
#define TRIE_KEY_EQUAL g_int_equal

// The library needs the following assertion:
// ASSERT(sizeof(void *) >= sizeof(int));

/* Trie node type */
typedef struct {

    GHashTable *map;                    /* Map code points to next trie node. */

    union {
        int val;
        void *ptr;
    } data;

    void (*dtor)(void *ptr);            /* The destructor. */

} trie_t;

/* Create a new trie root node. */
trie_t *trie_new();

/* Destroy the whole trie tree. */
void trie_destroy(trie_t *root);

/* Add new entry to the trie tree. */
void trie_enter(trie_t *start, char *path, void *data, int size, void (*dtor)(void *));

/* Follow the trie with the given code path, returns null if the target doesn't
   exist. */
trie_t *trie_find(trie_t *start, char *path);

#define TRIE_NODE_SIZE sizeof(trie_t)
#define TRIE_DATA_SIZE (TRIE_NODE_SIZE - sizeof(void *))

#endif
