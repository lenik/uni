include ../libauto/coolauto.am

dist_bashcmpl_DATA = \
    newmp

EXTRA_DIST = newmp.in
CLEANFILES = newmp

%: %.in Makefile
	$(do_subst) <$< >$@
