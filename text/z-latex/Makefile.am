include libauto/project.am

sitetexmfdir = ${datadir}/texmf
sitelatexdir = ${sitetexmfdir}/tex/latex

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

# rsync: -m: prune empty dirs
install-data-hook:
	mkdir -p "${DESTDIR}${sitetexmfdir}"
	rsync -amv \
            --exclude='.keep' \
            --exclude='.gitignore' \
            --exclude='ls-R' \
            "${srcdir}/texmf/" "${DESTDIR}${sitetexmfdir}"
