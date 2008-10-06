
SOURCES = $(wildcard *.cc)
TARGETS = $(subst .cc,.exe,$(SOURCES))
OUTDIR  = $(subst \,/,$(LAPIOTA))/bin
CC      = gcc

default: $(TARGETS)

%.exe: %.cc
	$(CC) -o $@ $*.cc
	cp -uf $@ $(OUTDIR)

clean:
	rm -f *.exe *.obj
