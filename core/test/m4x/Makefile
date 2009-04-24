
SOURCES = $(wildcard *.m4)
TARGETS = $(patsubst %.m4, %.out, $(SOURCES))

%.out: %.m4
	m4 -P -I$LAM_KALA/lib/m4 $^ >$@ 2>$*.err; \
	if [ ! -s $@.err ]; then rm $*.err; fi

%.t: %.m4
	m4 -P -I$LAM_KALA/lib/m4 $^

default: $(TARGETS)

clean:
	rm -f *.out
	rm -f *.err

# END
