
SOURCES=$(wildcard *.t)
TARGETS=$(subst .t,.o,$(SOURCES))

default: $(TARGETS)

%.o: %.t
	bash -c ./$^
