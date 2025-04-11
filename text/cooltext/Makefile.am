include libauto/project.am

dist_bin_SCRIPTS = \
    2utf8 \
    bobup \
    getrandomlines \
    grepstat \
    iconvf \
    ljoin \
    sortafter \
    tabify \
    tabsize \
    unindent \
    wcc
#    rjoin

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

install-data-hook:
	ln -sn sortafter ${DESTDIR}${bindir}/sorta

