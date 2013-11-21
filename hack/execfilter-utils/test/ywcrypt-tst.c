#include <stdio.h>
#include <string.h>

#include "../ywcrypt.h"

void main() {
    char buf[] = "hello, world";
    garble_encrypt(buf, strlen(buf));
    printf("enc: '%s'\n", buf);
    garble_decrypt(buf, strlen(buf));
    printf("dec: '%s'\n", buf);
}
