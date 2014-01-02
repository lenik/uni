include libauto/project.am

do_subst += -e 's,[@]vimconfdir[@],$(vimconfdir),g'

vimconfdir = ${sysconfdir}/vim

bin_PROGRAMS = \
    find-prefix

AM_CPPFLAGS = $(GLIB_CFLAGS)
find_prefix_LDADD = $(GLIB_LIBS)

find-prefix.c: find-prefix.c.in
	$(do_subst) <"$<" >"$@"

dist_bin_SCRIPTS = \
    coolvim \
    guesstab \
    ifedit \
    reload-file \
    vimcat

dist_pkgdata_DATA = \
    vimcat.vim

dist_vimconf_DATA = \
    vimrc.cool \
    reload.cfg

dist_alias_DATA = \
    aliases.d/coolvim.sh

dist_profile_DATA = \
    profile.d/coolvim.sh

dist_sudoers_DATA = \
    sudoers.d/env_editor

dist_man_MANS = \
    reload-file.1 \
    vimcat.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DATA = \
    postinst.in \
    prerm.in \
    reload-file.in

CLEANFILES = \
    ${dist_setup_SCRIPTS} \
    reload-file \
    vimcat

install-data-hook:
	cd $(DESTDIR)$(bindir); ln -snf coolvim coolvims
