#include <assert.h>

#include "ywcrypt.h"

#define K1 52899                        /* factors: 3 7 11 229 */
#define K2 22719                        /* factors: 3 7573 */

static void garble_encrypt(char *buf, int size) {
    int mid = size / 2;
    char *p = buf;
    char *h = buf + mid;
    int i;

    assert(buf);

    for (i = 0; i < mid; i++) {
        char t = *p;
        *p++ = *h;
        *h++ = t;
    }

    for (p = buf, i = 0; i < mid; i++)
        *p++ -= i;
    for (p = buf + mid, i = mid; i < size; i++)
        *p++ += size - i;
}

static void garble_decrypt(char *buf, int size) {
    int mid = size / 2;
    char *p = buf;
    char *h = buf + mid;
    int i;

    assert(buf);

    for (p = buf, i = 0; i < mid; i++)
        *p++ += i;
    for (p = buf + mid, i = mid; i < size; i++)
        *p++ -= size - i;

    for (p = buf, i = 0; i < mid; i++) {
        char t = *p;
        *p++ = *h;
        *h++ = t;
    }
}

#define xor_encrypt xor_crypt
#define xor_decrypt xor_crypt

static void xor_crypt(char *buf, int size) {
    char *p = buf;
    int i;
    unsigned int key = 102 * K1 + K2;

    for (i = 0; i < size; i++) {
        *p++ ^= key >> 8 | 1;
        key = (key + size) * K1 + K2;
    }
}

void yw_encrypt(char *buf, int size) {
    xor_encrypt(buf, size);
    garble_encrypt(buf, size);
}

void yw_decrypt(char *buf, int size) {
    garble_decrypt(buf, size);
    xor_decrypt(buf, size);
}
