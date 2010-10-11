#include <stdio.h>

#define MAX_LINE 100000

int line_index = 0;

char prefix[MAX_LINE];
int nprefix = 0;

char linebuf[MAX_LINE];

void process(FILE *f) {
  int i;
  while (fgets(linebuf, MAX_LINE, f)) {
    if (line_index == 0) {
      strcpy(prefix, linebuf);
      nprefix = strlen(prefix);
    } else {
      for (i = 0; i < nprefix; i++) {
	if (linebuf[i] != prefix[i]) {
	  nprefix = i;
	  break;
	}
      }
    }
    line_index++;
  }
}

int main(int argc, char **argv) {
  argc--;
  argv++;

  if (argc == 0) {
    process(stdin);
  } else {
    while (argc) {
      const char *filename = *argv++;
      FILE *f;
      argc--;
      f = fopen(filename, "r");
      if (f == NULL) {
	fprintf(stderr, "Can't open file %s. ", filename);
	perror("Open error: ");
	return 1;
      }
      process(f);
      fclose(f);
    }
  }

  prefix[nprefix] = '\0';
  puts(prefix);
  return 0;
}
