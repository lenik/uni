include libauto/coolauto.am

sitetexmfdir = ${datadir}/texmf
sitelatexdir = ${sitetexmfdir}/tex/latex

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

# rsync: -m: prune empty dirs
install-data-hook:
	mkdir -p "${DESTDIR}${sitetexmfdir}"
	rsync -amv \
            --exclude='.keep' \
            --exclude='.gitignore' \
            --exclude='ls-R' \
            "${srcdir}/texmf/" "${DESTDIR}${sitetexmfdir}"
