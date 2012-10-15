include libauto/coolauto.am

sited2dir = /usr/include/d2/site
# zdogdir = ${sited2dir}/net/bodz/dog

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

install-data-hook:
	mkdir -p $(DESTDIR)$(sited2dir)
	rsync -av src/ $(DESTDIR)$(sited2dir)
