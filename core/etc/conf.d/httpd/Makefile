
M4_INC      = $(LAM_KALA)/lib/m4
HCONF_M4    = $(M4_INC)/hconf.m4

all: httpd.conf

%: %.m4 $(HCONF_M4)
	m4 -I$(M4_INC) -P $*.m4 >$@
