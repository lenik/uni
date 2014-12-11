#include <stdio.h>
#include <string.h>
#include <bas/str.h>

void qstr_btok_test(char *s) {
    char *tok;
    int c = 0;
    s = strdup(s);

    while (tok = qstr_btok(s, &s, false)) {
        printf(" <%s>", tok);
        if (c++ > 10) {
            printf("~\n");
            c = 0;
        }
    }
    printf("\n");
}

int main() {
    qstr_btok_test("hello");
    qstr_btok_test("   hello, world   ");
    qstr_btok_test("foo   'bar  baz'    end");
    qstr_btok_test("first second\0third-is-bad");
    return 0;
}
