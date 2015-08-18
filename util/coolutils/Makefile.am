include libauto/project.am

colldir = /var/lib

AM_CPPFLAGS  = $(GLIB_CFLAGS)

%.c: %.c.in
	$(do_subst) $< >$@
#valsum_LDADD = $(GLIB_LIBS)

bin_PROGRAMS = \
    caseconv \
    gcd \
    random
#    valsum

dist_bin_SCRIPTS = \
    collect-copy \
    htmldoc-man \
    kill-all \
    timestamp \
    valcr \
    which2 \
    whichother

dist_completion_DATA = \
    bash_completion.d/coolutils.sh

dist_alias_DATA = \
    bash_aliases.d/coolutils.sh

dist_man_MANS = \
    caseconv.1 \
    collect-copy.1 \
    htmldoc-man.1

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
	install -d -m 2775 $(DESTDIR)$(colldir)

uninstall-hook:
	cd $(DESTDIR)$(bindir); \
		rm -f to-upper to-lower to-caption to-dash to-camel to-mech disfile
	cd $(DESTDIR)$(man1dir); \
		rm -f to-upper.1 to-lower.1 to-caption.1 to-dash.1 to-camel.1 to-mech.1
