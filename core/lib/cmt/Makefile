
YAPP=$(PERL) $(DIRT_HOME)/0/yapp.pl

all: oop_y.pm

oop_y.pm: oop_y.y
	$(YAPP) -m cmt::oop_y oop_y.y

clean:
	rm -f oop_y.output
	rm -f oop_y.pm
