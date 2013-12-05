include libauto/project.am

driverdir=${pkgdatadir}/driver

do_subst += -e 's,@driverdir@,${driverdir},g'

dist_bin_SCRIPTS = \
    coolsql

dist_driver_DATA = \
    pg.csd

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
