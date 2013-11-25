#include <stdio.h>
#include <string.h>

#include "../ywcrypt.h"

void main() {
    char buf[] = "hello, world";
    yw_encrypt(buf, strlen(buf));
    printf("enc: '%s'\n", buf);
    yw_decrypt(buf, strlen(buf));
    printf("dec: '%s'\n", buf);
}
