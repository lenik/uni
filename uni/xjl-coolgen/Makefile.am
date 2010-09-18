include libauto/coolauto.am

bin_SCRIPTS = \
    new

dist_man_MANS = \
    new.1

newtmpl_DATA =

EXTRA_DIST = \
	new.in \
	newtmpl.d

CLEANFILES = \
	new

%: %.in Makefile
	$(do_subst) <$< >$@

dist-hook:
	find $(distdir)/newtmpl.d -type d -name .svn -exec rm -fR {} \; -prune

install-data-hook:
	cp -RT $(srcdir)/newtmpl.d $(DESTDIR)$(newtmpldir)
	find $(DESTDIR)$(newtmpldir) -type d -name .svn -exec rm -fR {} \; -prune
