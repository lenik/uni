include libauto/coolauto.am

dist_aliases_DATA = \
    fileutils.sh

dist_bin_SCRIPTS = \
    textp \
    binaryp \
    swap \
    enfile \
    symfl \
    symflatten \
    mv-p \
    rename-p \
    coolmore \
    tourl

dist_man_MANS = \
    symfl.1 \
    mv-p.1

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

install-data-hook:
	cd $(DESTDIR)$(bindir); \
		$(LN_S) enfile disfile
