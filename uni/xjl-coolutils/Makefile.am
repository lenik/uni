include libauto/coolauto.am

colldir = /var/lib

AM_CPPFLAGS  = $(GLIB_CFLAGS)
valsum_LDADD = $(GLIB_LIBS)

bin_PROGRAMS = \
    uc \
    gcd \
    random
#    valsum

dist_bin_SCRIPTS = \
    which2 \
    whichother \
    hm \
    collect-copy \
    kill-all

dist_man_MANS = \
    uc.1 \
    hm.1 \
    collect-copy.1

install-data-hook:
	cd $(DESTDIR)$(bindir); \
		$(LN_S) -f uc lc; \
		$(LN_S) -f uc to-caption; \
		$(LN_S) -f uc to-dash; \
		$(LN_S) -f uc to-camel; \
		$(LN_S) -f uc to-mech
	cd $(DESTDIR)$(man1dir); \
		$(LN_S) -f uc.1 lc.1; \
		$(LN_S) -f uc.1 to-caption.1; \
		$(LN_S) -f uc.1 to-dash.1; \
		$(LN_S) -f uc.1 to-camel.1; \
		$(LN_S) -f uc.1 to-mech.1
	install -d -m 2775 $(DESTDIR)$(colldir)

uninstall-hook:
	cd $(DESTDIR)$(bindir); \
		rm -f lc to-caption to-dash to-camel to-mech disfile
	cd $(DESTDIR)$(man1dir); \
		rm -f lc.1 to-caption.1 to-dash.1 to-camel.1 to-mech.1
