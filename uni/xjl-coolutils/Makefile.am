
include libauto/subst.am

bin_PROGRAMS = \
    uc \
	gcd \
	random

dist_bin_SCRIPTS = \
	binaryp \
	textp \
    swap \
    which2 \
    whichother \
    df-x \
    sedit \
    headrm \
    grepl \
	hm \
	lineconf \
	enfile

dist_man_MANS = \
    sedit.1 \
	uc.1 \
	hm.1

install-data-hook:
	cd $(DESTDIR)$(bindir); \
		$(LN_S) -f uc lc; \
		$(LN_S) -f uc to-caption; \
		$(LN_S) -f uc to-dash; \
		$(LN_S) -f uc to-camel; \
		$(LN_S) -f uc to-mech; \
		$(LN_S) -f enfile disfile
	cd $(DESTDIR)$(man1dir); \
		$(LN_S) -f uc.1 lc.1; \
		$(LN_S) -f uc.1 to-caption.1; \
		$(LN_S) -f uc.1 to-dash.1; \
		$(LN_S) -f uc.1 to-camel.1; \
		$(LN_S) -f uc.1 to-mech.1

uninstall-hook:
	cd $(DESTDIR)$(bindir); \
		rm -f lc to-caption to-dash to-camel to-mech disfile
	cd $(DESTDIR)$(man1dir); \
		rm -f lc.1 to-caption.1 to-dash.1 to-camel.1 to-mech.1
