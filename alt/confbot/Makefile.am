include libauto/project.am

do_subst += \
    -e 's,[@]botsdir[@],$(botsdir),g'

botsdir = ${sysconfdir}/bots

dist_bin_SCRIPTS = \
    confbot-scan

dist_init_DATA = \
    confbot.conf

dist_bots_DATA = \
    sampleconf.ini.1

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS} \
    confbot-scan \
    confbot.conf

install-data-hook:
	cp -a ${srcdir}/bot ${DESTDIR}${pkgdatadir}
	ln -snf ${pkgdatadir}/confbot.py ${DESTDIR}${bindir}/confbot
