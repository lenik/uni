include libauto/project.am

dist_bin_SCRIPTS = \
    xselproc

#dist_alias_DATA = \
#    xsel-enh.sh

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	cd ${DESTDIR}${bindir}; \
	    ln -sn xselproc b

