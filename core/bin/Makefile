
PERLCC=perlcc.bat

all: *.exe *.pc

%.exe: %.c
	gcc -o $@ $^

%.pc: %.pl
	$(PERLCC) -B -o $@ $^

clean:
	del *.obj
