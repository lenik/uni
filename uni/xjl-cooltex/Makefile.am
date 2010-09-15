
include libauto/coolauto.am

setupdir = ${datadir}/setup/${PACKAGE_NAME}

dist_bin_SCRIPTS = \
	pdfmon

setup_SCRIPTS = \
    post-install.sh \
    pre-remove.sh

EXTRA_DIST = \
	post-install.sh.in \
	pre-remove.sh.in

CLEANFILES = \
	post-install.sh \
	pre-remove.sh

%: %.in Makefile
	$(do_subst) <$< >$@
