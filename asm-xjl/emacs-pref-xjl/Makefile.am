include libauto/project.am

progmodesdir = ${lispdir}/progmodes

dist_alias_DATA = \
    emacs-aliases.sh

dist_lisp_LISP = \
    color-theme.el \
    coolemacs.el

color-theme.el: Makefile.am
	wget -qO- http://download.savannah.nongnu.org/releases/color-theme/color-theme.el.gz \
	    | gunzip -c >$@

progmodes_LISP = \
    contrib/d-mode.el

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    postinst \
    prerm
