include libauto/coolauto.am

SUBDIRS = test

svnuniondir = ${sysconfdir}/svn_union.d

dist_bin_SCRIPTS = \
	coolsvn \
	svn-002 \
	svn-apply \
	svn-backup-sync \
	svn-dirty-dirs  \
	svn-isclean \
	svn-ignore \
	svn-ignore-unversioned \
	svn-remix \
	svn-rename \
	svn-ps \
	svn-start-rev \
	svn-show-usedkeywords \
	svn-swf \
	svn-update-keywords \
	svn-update-executable \
	svn-union \
	svn-union-install \
	svnpsl \
	svnserve-002 \
	svnadmin-authz-update

dist_man_MANS = \
	coolsvn.1 \
	svn-rename.1 \
	svn-union.1 \
	svn_union.d.5 \
	svn-swf.1 \
	svnpsl.1

dist_svnunion_DATA = \
	svn_union.d/README

dist_aliases_DATA = \
	aliases.d/coolsvn.sh

EXTRA_DIST = svn-union.in svn-union-install.in
CLEANFILES = svn-union    svn-union-install

%: %.in Makefile
	$(do_subst) \
		-e 's,[@]svnuniondir[@],$(svnuniondir),g' \
		<$< >$@

