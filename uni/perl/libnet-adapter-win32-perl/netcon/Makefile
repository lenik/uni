
INC:=$(addprefix -I,$(subst ;, ,$(INCLUDE)))
LIB:=$(addprefix -L,$(subst ;, ,$(LIB)))
CCFLAGS:=$(CCFLAGS) $(INC) $(LIB)

OUTDIR=../blib/arch/auto/Net/Adapter/

all: $(OUTDIR)test.exe

$(OUTDIR)%.exe: %.c netcon.h
	gcc $(CCFLAGS) -o $@ $*.c
