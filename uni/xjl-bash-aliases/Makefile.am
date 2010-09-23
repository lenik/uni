include libauto/coolauto.am

aliases_DATA =

dist_sysconf_DATA = \
    bash_aliases

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
	postinst.in \
	prerm.in \
        aliases.d

CLEANFILES = ${setup_SCRIPTS}

dist-hook:
	find $(distdir)/aliases.d -type d -name .svn -exec rm -fR {} \; -prune

install-data-hook:
	cp -RT $(srcdir)/aliases.d $(DESTDIR)$(aliasesdir)
	find $(DESTDIR)$(aliasesdir) -type d -name .svn -exec rm -fR {} \; -prune

%: %.in Makefile
	$(do_subst) <$< >$@
