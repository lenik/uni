include libauto/project.am

dist_alias_DATA = \
    fileutils.sh

dist_bin_SCRIPTS = \
    binaryp \
    chmod-autofix \
    coolmore \
    enfile \
    fix-symlinks \
    mv-p \
    overlay-ln \
    ren-mkdirs \
    ren-r \
    ren-timever \
    swap \
    symfl \
    symflatten \
    textp \
    tourl

dist_man_MANS = \
    chmod-autofix.1 \
    fix-symlinks.8 \
    ren-timever.1 \
    symfl.1 \
    mv-p.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

install-data-hook:
	cd $(DESTDIR)$(bindir); \
        $(LN_S) enfile disfile
