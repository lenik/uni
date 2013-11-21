#ifndef __CRYPT_H
#define __CRYPT_H

void garble_encrypt(char *buf, int size);

void garble_decrypt(char *buf, int size);

void xor_crypt(char *buf, int size);

#define xor_encrypt xor_crypt
#define xor_decrypt xor_crypt

#endif
