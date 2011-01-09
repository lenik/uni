
SOURCES = $(wildcard *.c)
TARGETS = $(subst .c,.exe,$(SOURCES))
OUTDIR  = $(subst \,/,$(LAPIOTA))/bin

default: $(TARGETS)

%.exe: %.c
	gcc -o $@ $*.c
	cp -uf $@ $(OUTDIR)

clean:
	rm *.exe
