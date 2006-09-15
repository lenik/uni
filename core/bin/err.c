#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv) {
    long e;
	if (argc < 2) {
		printf("Err <errval>\n");
		return 0;
	}
	e = strtol(argv[1], NULL, 0);
	return e;
}
