include libauto/project.am

dist_aliases_DATA = \
    fileutils.sh

dist_bin_SCRIPTS = \
    binaryp \
    coolmore \
    enfile \
    mv-p \
    overlay-ln \
    ren-mkdirs \
    ren-r \
    swap \
    symfl \
    symflatten \
    textp \
    tourl

dist_man_MANS = \
    symfl.1 \
    mv-p.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

install-data-hook:
	cd $(DESTDIR)$(bindir); \
        $(LN_S) enfile disfile
