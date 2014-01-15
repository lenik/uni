#include <assert.h>

#define K1 52899                        /* factors: 3 7 11 229 */
#define K2 22719                        /* factors: 3 7573 */

void garble_encrypt(char *buf, int size) {
    int mid = size / 2;
    char *p = buf;
    char *h = buf + mid;
    int i;

    assert(buf);

    // �򵥵�ǰ���û�
    for (i = 0; i < mid; i++) {
        char t = *p;
        *p++ = *h;
        *h++ = t;
    }

    // ʹ�ü��Ϸ��ض�(����һ������)��������γɻ�������
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

    // ʹ�ü��Ϸ��ض�(����һ������)������������������
    for (p = buf, i = 0; i < mid; i++)
        *p++ += i;
    for (p = buf + mid, i = mid; i < size; i++)
        *p++ -= size - i;

    // �򵥵�ǰ���û�
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
