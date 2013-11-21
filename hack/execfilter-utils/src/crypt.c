#include <assert.h>

#define K1 52899                        /* factors: 3 7 11 229 */
#define K2 22719                        /* factors: 3 7573 */

void garble_encrypt(char *buf, int size) {
    int mid = size / 2;
    char *p = buf;
    char *h = buf + mid;
    int i;

    assert(buf);

    // 简单的前后置换
    for (i = 0; i < mid; i++) {
        char t = *p;
        *p++ = *h;
        *h++ = t;
    }

    // 使用加上非特定(但有一定规则)随机数来形成混淆方案
    for (p = buf, i = 0; i < mid; i++)
        *p++ -= i;
    for (p = buf + mid, i = mid; i < size; i++)
        *p++ += size - i;
}

void garble_decrypt(char *buf, int size) {
    int mid = size / 2;
    char *p = buf;
    char *h = buf + mid;
    int i;

    assert(buf);

    // 使用加上非特定(但有一定规则)随机数来解除混淆方案
    for (p = buf, i = 0; i < mid; i++)
        *p++ += i;
    for (p = buf + mid, i = mid; i < size; i++)
        *p++ -= size - i;

    // 简单的前后置换
    for (p = buf, i = 0; i < mid; i++) {
        char t = *p;
        *p++ = *h;
        *h++ = t;
    }
}

void xor_crypt(char *buf, int size) {
    char *p = buf;
    int i;
    unsigned int key = 102 * K1 + K2;

    for (i = 0; i < size; i++) {
        *p++ ^= (key >> 8) | 1;
        key = (key + size) * K1 + K2;
    }
}
