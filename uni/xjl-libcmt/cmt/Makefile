
SHELL       = bash
MAKEFLAGS   = --unix

PERL        = $(shell realpath $$PERL)
YAPP        = $(PERL) $(shell rpath.bat '$(DIRT_HOME)/0/yapp.pl')

all: oop_y.pm

oop_y.pm: oop_y.y
	$(YAPP) -v -m cmt::oop_y oop_y.y
	@if ! grep    Conflict  oop_y.output; then rm oop_y.output; fi
	@if   grep -q Warnings: oop_y.output; then less oop_y.output; fi

clean:
	rm -f oop_y.output
	rm -f oop_y.pm
