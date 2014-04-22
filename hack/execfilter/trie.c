#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "trie.h"

#define CHECK_ALLOC(p) do { \
    if ((p) == NULL) { \
        fprintf(stderr, "Memory out!\n"); \
        exit(1); \
    } \
    } while (0)

trie_t *trie_new() {
    trie_t *root = (trie_t *) malloc(TRIE_NODE_SIZE);
    CHECK_ALLOC(root);

    root->map = g_hash_table_new_full(TRIE_KEY_HASH,
                                      TRIE_KEY_EQUAL,
                                      NULL, /* key dtor */
                                      (GDestroyNotify) &trie_destroy);
    CHECK_ALLOC(root->map);

    return root;
}

void trie_destroy(trie_t *root) {
    if (root->map) {
        g_hash_table_destroy(root->map);
        root->map = NULL;
    }

    if (root->dtor)
        root->dtor(root->data.ptr);

    free(root);
}

trie_t *trie_find(trie_t *start, char *path) {
    if (! *path)
        return start;

    char code = *path++;
    trie_t *next = g_hash_table_lookup(start->map, *(gconstpointer *) &code);
    if (next == NULL)
        return NULL;

    return trie_find(next, path);
}

void trie_enter(trie_t *start, char *path, void *data, int size, void (*dtor)(void *) dtor) {
    if (! *path) {
        if (data)
            memcpy(&(start->data), data, size);
        start->dtor = dtor;
        return;
    }

    char code = *path++;
    trie_t *next = g_hash_table_lookup(start->map, *(gconstpointer *) &code);
    if (next == NULL) {
        next = trie_new();
        CHECK_ALLOC(next);
        g_hash_table_insert(start->map, *(gpointer *) &code, next);
    }

    trie_enter(next, path, data, size, dtor);
}
