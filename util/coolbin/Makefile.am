include libauto/project.am

AM_CPPFLAGS = $(GLIB_CFLAGS)
chop_LDADD = $(GLIB_LIBS)
xcat_LDADD = $(GLIB_LIBS)

%.c: %.c.in
	$(do_subst) $< >$@
#valsum_LDADD = $(GLIB_LIBS)

bin_PROGRAMS = \
    caseconv \
    gcd \
    random \
    chop \
    echo-exec \
    echo-n \
    println \
    xcat
#    valsum

dist_man_MANS = \
    caseconv.1 \
    xcat.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	cd $(DESTDIR)$(bindir); \
		$(LN_S) -f caseconv to-upper; \
		$(LN_S) -f caseconv to-lower; \
		$(LN_S) -f caseconv to-caption; \
		$(LN_S) -f caseconv to-dash; \
		$(LN_S) -f caseconv to-camel; \
		$(LN_S) -f caseconv to-mech
	cd $(DESTDIR)$(man1dir); \
		$(LN_S) -f caseconv.1 to-upper.1; \
		$(LN_S) -f caseconv.1 to-lower.1; \
		$(LN_S) -f caseconv.1 to-caption.1; \
		$(LN_S) -f caseconv.1 to-dash.1; \
		$(LN_S) -f caseconv.1 to-camel.1; \
		$(LN_S) -f caseconv.1 to-mech.1

uninstall-hook:
	cd $(DESTDIR)$(bindir); \
		rm -f to-upper to-lower to-caption to-dash to-camel to-mech disfile
	cd $(DESTDIR)$(man1dir); \
		rm -f to-upper.1 to-lower.1 to-caption.1 to-dash.1 to-camel.1 to-mech.1
