#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main() {
    char **lp = DATA;
    char *geom = *lp++;
    int columns;
    int rows;
    int colors;
    int cpp;                            /* chars-per-pixel */
    int ctbl[256];
    char *cnam[256];

    sscanf(geom, "%d %d %d %d", &columns, &rows, &colors, &cpp);
    if (cpp != 1) {
        fprintf(stderr, "Unsupported chars-per-pixel.\n");
        return 1;
    }

    memset(ctbl, 0xff, sizeof(ctbl));
    memset(cnam, 0, sizeof(cnam));
    
    for (int c = 0; c < colors; c++) {
        /* ". c white" */
        char *cdef = *lp++;
        char *p = cdef;
        int ch = *p++;
        if (strncmp(p, " c ", 3) != 0) {
            fprintf(stderr, "Bad cdef[%d]: %s\n", c, cdef);
            return 1;
        }
        p += 3;
        cnam[c] = p;
        if (strcmp(p, "white") == 0)
            ctbl[ch] = 0;
        if (strcmp(p, "black") == 0)
            ctbl[ch] = 1;
    }
    
    for (int y = 0; y < rows; y++) {
        char *p = *lp++;
        int ch;
        int x = 0;
        int byt = 0;
        while (ch = *p++) {
            int clr = ctbl[ch];
            byt <<= 1;
            if (clr)
                byt |= 1;
            x++;
            if (x % 8 == 0) {
                putchar(byt);
                byt = 0;
            }
        }
        if (x %= 8) {                   /* align on 8-bit */
            while (x++ < 8)
                byt <<= 1;
            putchar(byt);
        }
    }
    return 0;
}
