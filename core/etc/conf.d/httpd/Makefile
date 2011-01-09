
M4_INC      = $(LAM_KALA)/lib/m4
HCONF_M4    = $(M4_INC)/hconf.m4

all: httpd.conf

httpd.conf: httpd.conf.m4 ~/etc/httpd.conf $(HCONF_M4)
	m4 -I$(M4_INC) -P httpd.conf.m4 >httpd.conf

clean:
	rm -f httpd.conf
